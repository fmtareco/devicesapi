-- $(flyway:timestamp)
-- repeatable migration

CREATE OR REPLACE VIEW v_brand_summary AS
select
    brand,
    count(*) as num_products
FROM devices
GROUP BY brand;