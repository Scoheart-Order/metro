-- Create views to allow compatibility between singular and plural table names
-- This allows the demo.sql which uses plural names to work with init.sql which creates singular names

-- Create view for lines
CREATE OR REPLACE VIEW lines AS 
SELECT * FROM line;

-- Create view for stations
CREATE OR REPLACE VIEW stations AS 
SELECT * FROM station;

-- Create view for routes
CREATE OR REPLACE VIEW routes AS 
SELECT * FROM route;

-- Create view for stops
CREATE OR REPLACE VIEW stops AS 
SELECT * FROM stop;

-- Make sure insertions to the views affect the actual tables
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- Trigger for lines
DELIMITER //
CREATE TRIGGER insert_lines_trigger 
INSTEAD OF INSERT ON lines
FOR EACH ROW 
BEGIN
    INSERT INTO line(id, name, code, color, operator) 
    VALUES(NEW.id, NEW.name, NEW.code, NEW.color, NEW.operator);
END;
//
DELIMITER ;

-- Trigger for stations
DELIMITER //
CREATE TRIGGER insert_stations_trigger 
INSTEAD OF INSERT ON stations
FOR EACH ROW 
BEGIN
    INSERT INTO station(id, name, code, address, is_transfer_station) 
    VALUES(NEW.id, NEW.name, NEW.code, NEW.address, NEW.is_transfer_station);
END;
//
DELIMITER ;

-- Trigger for routes
DELIMITER //
CREATE TRIGGER insert_routes_trigger 
INSTEAD OF INSERT ON routes
FOR EACH ROW 
BEGIN
    INSERT INTO route(id, line_id, name, start_station_id, end_station_id) 
    VALUES(NEW.id, NEW.line_id, NEW.name, NEW.start_station_id, NEW.end_station_id);
END;
//
DELIMITER ;

-- Trigger for stops
DELIMITER //
CREATE TRIGGER insert_stops_trigger 
INSTEAD OF INSERT ON stops
FOR EACH ROW 
BEGIN
    INSERT INTO stop(id, route_id, station_id, seq, is_transfer) 
    VALUES(NEW.id, NEW.route_id, NEW.station_id, NEW.seq, NEW.is_transfer);
END;
//
DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE; 