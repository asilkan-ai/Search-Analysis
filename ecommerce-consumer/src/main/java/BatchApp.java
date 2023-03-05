import com.mongodb.spark.MongoSpark;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import  org.apache.spark.sql.functions;

public class BatchApp {
    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir", "C:\\hadoop-common-2.2.0-bin-master"); //to avoid Hadoop binary error

        //create schema of data to be read from kafka
        StructType schema=new StructType().add("search", DataTypes.StringType)
                .add("region",DataTypes.StringType)
                .add("current_ts",DataTypes.StringType)
                .add("userId",DataTypes.IntegerType);

        SparkSession sparkSession = SparkSession.builder().master("local").appName("AppName")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/db_name").getOrCreate();
        //getting data from kafka
        Dataset<Row> loadDS = sparkSession.read().format("kafka")
                .option("kafka.bootstrap.servers", "127.0.0.1:9092")
                .option("subscribe", "search-data").load();
        //casting value from binary to string and wearing the schema
        Dataset<Row> rowDataset = loadDS.selectExpr("CAST(value AS STRING)");
        Dataset<Row> valueDS = rowDataset.select(functions.from_json(rowDataset.col("value"), schema)
                        .as("jsontostructs")).select("jsontostructs.*");

        //top 10 searched products
        Dataset<Row> searchGroup = valueDS.groupBy("search").count();
        Dataset<Row> searchResult = searchGroup.sort(functions.desc("count")).limit(10);;
        //register to MongoDB
        MongoSpark.write(searchResult).option("collection","top_products").mode("overwrite").save();

        //how many times did each user search for which product?
        Dataset<Row> count = valueDS.groupBy("userId", "search").count();
        Dataset<Row> pivot = count.groupBy("userId").pivot("search").sum("count").na().fill(0);
        //register to MongoDB
        MongoSpark.write(pivot).option("collection","user_searches").mode("overwrite").save();

        //analysis of the busiest hours of the day
        Dataset<Row> windowedDS = valueDS.groupBy(functions.window(valueDS.col("current_ts"),
                "30 minute"), valueDS.col("search")).count();
        Dataset<Row> sumDS = windowedDS.groupBy("window").pivot("search").sum("count");
        //register to MongoDB
        MongoSpark.write(sumDS).option("collection","windowed_searches").save();

    }
}
