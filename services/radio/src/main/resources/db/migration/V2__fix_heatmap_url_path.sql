-- V2__fix_heatmap_url_path.sql

-- Case 1: "heatmaps/30_gradcam.jpg" → "heatmap-bucket/heatmaps/30_gradcam.jpg"
UPDATE radios
SET heatmap_url = CONCAT('heatmap-bucket/', heatmap_url),
    updated_at  = NOW()
WHERE heatmap_url LIKE 'heatmaps/%'
  AND heatmap_url NOT LIKE 'heatmap-bucket/%';

-- Case 2: "heatmap-bucket/30_gradcam.jpg" → "heatmap-bucket/heatmaps/30_gradcam.jpg"
UPDATE radios
SET heatmap_url = REPLACE(heatmap_url, 'heatmap-bucket/', 'heatmap-bucket/heatmaps/'),
    updated_at  = NOW()
WHERE heatmap_url LIKE 'heatmap-bucket/%'
  AND heatmap_url NOT LIKE 'heatmap-bucket/heatmaps/%';