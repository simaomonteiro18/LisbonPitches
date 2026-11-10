-- Os links do aircourts deixaram de funcionar (redirecionam para a home do Playtomic).
-- Sem imagem, o frontend usa a vista de satelite do Mapbox.
UPDATE pitches SET image_url = NULL
WHERE image_url LIKE '%aircourts%';
