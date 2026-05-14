docker run --rm -v $(pwd):/data --network host dimitri/pgloader:latest pgloader  --debug /data/migrate.load
