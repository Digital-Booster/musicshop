docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest pgloader  --debug /data/migrate.load


# docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest \
#   pgloader data/MusicShopSQLite.db postgresql://postgres:postgres@localhost:5432/musicshop
