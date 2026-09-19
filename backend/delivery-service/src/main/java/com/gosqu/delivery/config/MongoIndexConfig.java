package com.gosqu.delivery.config;

import com.gosqu.delivery.tracking.DeliveryTracking;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

@Configuration
public class MongoIndexConfig {

    @Bean
    public CommandLineRunner ensureGeoIndex(MongoTemplate mongoTemplate) {
        return _ -> mongoTemplate.indexOps(DeliveryTracking.class)
                .createIndex(new GeospatialIndex("currentLocation").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }
}
