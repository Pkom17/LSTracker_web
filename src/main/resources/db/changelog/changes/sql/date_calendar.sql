-- Create date calendar
DROP TABLE if exists sample_tracker.date_calendar;

CREATE TABLE sample_tracker.date_calendar (
date_id INT NOT NULL,
date DATE NOT NULL,
day_name VARCHAR(9) NOT NULL,
day_name_abbr VARCHAR(9) NOT NULL,
day_of_week INT NOT NULL,
day_of_month INT NOT NULL,
day_of_year INT NOT NULL,
month_ INT NOT NULL,
month_name VARCHAR(9) NOT NULL,
month_name_abbr CHAR(3) NOT NULL,
quarter_ INT NOT NULL,
quarter_name VARCHAR(9) NOT NULL,
year_ INT NOT NULL,
yyyymm VARCHAR NOT NULL,
yyyymmdd VARCHAR NOT NULL,
"Year" VARCHAR,
"Month" VARCHAR,
"Quarter" VARCHAR);
ALTER TABLE sample_tracker.date_calendar ADD CONSTRAINT date_calendar_date_pk PRIMARY KEY (date_id);
CREATE INDEX date_calendar_date_ac_idx ON sample_tracker.date_calendar(date);

INSERT INTO sample_tracker.date_calendar
SELECT TO_CHAR(datum,'yyyymmdd')::INT AS date_id,
datum AS date,
TO_CHAR(datum,'Day') AS day_name,
TO_CHAR(datum,'Dy') AS day_name_abbr,
EXTRACT(isodow FROM datum) AS day_of_week,
EXTRACT(DAY FROM datum) AS day_of_month,
EXTRACT(doy FROM datum) AS day_of_year,
EXTRACT(MONTH FROM datum) AS month_,
TO_CHAR(datum,'Month') AS month_name,
TO_CHAR(datum,'Mon') AS month_name_abbr,
EXTRACT(quarter FROM datum) AS quarter_,
CONCAT('Q',EXTRACT(quarter FROM datum)) quarter_name,
EXTRACT(isoyear FROM datum) AS year_,
TO_CHAR(datum,'yyyymm') AS yyyymm,
TO_CHAR(datum,'yyyymmdd') AS yyyymmdd,
EXTRACT(year FROM datum) "Year",
CONCAT(EXTRACT(year FROM datum),'-',TO_CHAR(datum,'Mon'))  "Month",
CONCAT(EXTRACT(year FROM datum),'-Q',EXTRACT(quarter FROM datum)) "Quarter" FROM (SELECT datum::date FROM GENERATE_SERIES (
    DATE '2020-01-01', 
    DATE '2030-12-31', 
    INTERVAL '1 day'
) AS datum) dates_series;