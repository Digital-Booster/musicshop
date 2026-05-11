docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest pgloader /data/migrate.load
