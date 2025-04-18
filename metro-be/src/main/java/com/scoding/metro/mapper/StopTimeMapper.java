package com.scoding.metro.mapper;

import com.scoding.metro.entity.StopTime;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StopTimeMapper {
    
    @Select("SELECT * FROM stop_time")
    List<StopTime> selectAll();
    
    @Select("SELECT * FROM stop_time WHERE id = #{id}")
    StopTime selectById(Long id);
    
    @Select("SELECT * FROM stop_time WHERE train_trip_id = #{trainTripId} ORDER BY stop_seq")
    List<StopTime> selectByTrainTripId(Long trainTripId);
    
    @Select("SELECT * FROM stop_time WHERE stop_id = #{stopId}")
    List<StopTime> selectByStopId(Long stopId);
    
    @Insert("INSERT INTO stop_time(train_trip_id, stop_id, arrival_time, departure_time, stop_seq) " +
            "VALUES(#{trainTripId}, #{stopId}, #{arrivalTime}, #{departureTime}, #{stopSeq})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StopTime stopTime);
    
    @Update("UPDATE stop_time SET train_trip_id = #{trainTripId}, stop_id = #{stopId}, " +
            "arrival_time = #{arrivalTime}, departure_time = #{departureTime}, " +
            "stop_seq = #{stopSeq} WHERE id = #{id}")
    int update(StopTime stopTime);
    
    @Delete("DELETE FROM stop_time WHERE id = #{id}")
    int delete(Long id);
} 