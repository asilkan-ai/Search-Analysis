import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class StreamingApp {
    public static void main(String[] args) throws StreamingQueryException {
        System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master"); //to avoid Hadoop binary error
        //create schema of data to be read from kafka
        StructType schema=new StructType().add("search", DataTypes.StringType)
                .add("region",DataTypes.StringType)
                .add("current_ts",DataTypes.StringType)
                .add("userId",DataTypes.IntegerType);

        SparkSession sparkSession = SparkSession.builder().master("local").appName("AppName")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/db_name").getOrCreate();

        //streaming data from kafka
        Dataset<Row> loadDS = sparkSession.readStream().format("kafka")
                .option("kafka.bootstrap.servers", "127.0.0.1:9092")
                .option("subscribe", "search-data").load();

        //casting value from binary to string and wearing the schema
        Dataset<Row> rowDataset = loadDS.selectExpr("CAST(value AS STRING)");
        Dataset<Row> valueDS = rowDataset.select(functions.from_json(rowDataset.col("value"), schema)
                .as("jsontostructs")).select("jsontostructs.*");

        //filtering users looking for laptops and saving to MongoDB
        Dataset<Row> laptopFilter = valueDS.filter(valueDS.col("search").equalTo("laptop"));
        laptopFilter.writeStream().foreachBatch(new VoidFunction2<Dataset<Row>, Long>() {
            @Override
            public void call(Dataset<Row> rowDataset, Long aLong) throws Exception {
                MongoSpark.write(rowDataset).option("collection","laptop_users").mode("append").save();
            }
        }).start().awaitTermination();

    }
}
