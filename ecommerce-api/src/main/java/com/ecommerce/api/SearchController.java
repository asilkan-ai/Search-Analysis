package com.ecommerce.api;

import com.ecommerce.producer.ProducerKafka;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class SearchController {
    @Autowired
    ProducerKafka producerKafka;
    @GetMapping("/search")
    public void searchIndex(@RequestParam String term) {
        //random city list
        List<String> cities = Arrays.asList("Ankara", "İstanbul", "Adana", "Mersin", "Trabzon",
                "Çanakkale", "Elazığ", "Bursa", "İzmir", "Tekirdağ", "Zonguldak");
        //random product list
        List<String> products = Arrays.asList("Telefon", "Bebek Bezi", "Laptop",
                "Klavye", "Ütü", "Cüzdan", "Paspas", "Mont", "Hırka", "Gömlek", "Kol Saati", "Çanta", "Ayna", "Bardak");
        //adding the date
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        while (true) {
            Random random = new Random();
            int i = random.nextInt(cities.size()); //random city selection
            int j = random.nextInt(products.size()); //random product selection
            long offset = Timestamp.valueOf("2023-03-01 02:00:00").getTime();
            long end = Timestamp.valueOf("2023-03-01 23:59:00").getTime();
            long diff = end - offset + 1;
            Timestamp randtime = new Timestamp(offset + (long) (Math.random() * diff));

            //convert data into json format
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("search", products.get(i));
            jsonObject.put("current_ts", randtime.toString());
            jsonObject.put("region", cities.get(i));
            jsonObject.put("userId",random.nextInt(15000-1000)+1000);

            System.out.println(jsonObject.toJSONString());
            producerKafka.send(jsonObject.toJSONString()); //JSON data was produced to Kafka
        }
    }
    @GetMapping("/search/stream")
    public void searchIndexStream(@RequestParam String term) {
        List<String> cities = Arrays.asList("Ankara", "İstanbul", "Adana", "Mersin", "Trabzon",
                "Çanakkale", "Elazığ", "Bursa", "İzmir", "Tekirdağ", "Zonguldak");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis()); //adding the date

        Random random = new Random();
        int i = random.nextInt(cities.size()); //random city selection

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("search", term);
        jsonObject.put("current_ts", timestamp.toString());
        jsonObject.put("region", cities.get(i));
        jsonObject.put("userId",random.nextInt(15000-1000)+1000);

        System.out.println(jsonObject.toJSONString());
        producerKafka.send(jsonObject.toJSONString()); //JSON data was produced to Kafka
    }
}
