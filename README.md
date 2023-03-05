# Search Analysis in E-commerce(Big Data Project)
User searches are very important in e-commerce. That's why e-commerce companies analyze user searches. As a result of the analysis, it can recommend products or offer campaigns to users. In this way, companies sell more products.
<br>
![model](https://user-images.githubusercontent.com/73762823/222971438-13ea7e24-8f96-446c-a6fd-b41475aa4f0d.png)
<br>
In this project, user searches are taken from the front end and random userid, city, date and time values are added to these searches on the backend. This method was applied because real data mustn't be used. The data is then produced to Kafka. In the Spark part, data is consumed for analysis. Sample scenarios have been prepared for both batch and streaming analysis. And finally, its data is saved in MongoDB.
