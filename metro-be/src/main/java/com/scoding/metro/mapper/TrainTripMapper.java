package com.scoding.metro.mapper;

import com.scoding.metro.entity.TrainTrip;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainTripMapper {
    
    @Select("SELECT * FROM train_trip")
    List<TrainTrip> selectAll();
    
    @Select("SELECT * FROM train_trip WHERE id = #{id}")
    TrainTrip selectById(Long id);
    
    @Select("SELECT * FROM train_trip WHERE route_id = #{routeId}")
    List<TrainTrip> selectByRouteId(Long routeId);
    
    @Insert("INSERT INTO train_trip(route_id, train_number, run_date) " +
            "VALUES(#{routeId}, #{trainNumber}, #{runDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TrainTrip trainTrip);
    
    @Update("UPDATE train_trip SET route_id = #{routeId}, train_number = #{trainNumber}, " +
            "run_date = #{runDate} WHERE id = #{id}")
    int update(TrainTrip trainTrip);
    
    @Delete("DELETE FROM train_trip WHERE id = #{id}")
    int delete(Long id);
} 