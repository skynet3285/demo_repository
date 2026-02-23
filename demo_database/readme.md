```sh
docker run -d \
  --name demo-user-db \
  -e POSTGRES_USER=demo_userdb \
  -e POSTGRES_PASSWORD=demo_userdb \
  -e POSTGRES_DB=demo_user \
  -v $(pwd)/init.sql:/docker-entrypoint-initdb.d/schema.sql \
  -p 5432:5432 \
  postgres:18.1
```
