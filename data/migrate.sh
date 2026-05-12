docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest pgloader /data/migrate.load


# docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest \
#   pgloader data/MusicShopSQLite.db postgresql://postgres:postgres@localhost:5432/musicshop
