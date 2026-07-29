### 
# git clone https://github.com/Digital-Booster/musicshop.git
#
export YOUR_PROJECT_ID= # Google Project ID
export YOUR_REGION= # insert your GCP region
export YOUR_SERVICE="`echo $LOGNAME | tr '_' '-'`-musicshop"
export PSQL_IP= # insert PostgreSQL Host IP 
export PSQL_USER=postgres
export PSQL_PWD= # your password
export YOUR_NETWORK= # your VPC 
export YOUR_SUBNET= # your VPC Subet


# REMOTE BUILD
gcloud builds submit --tag gcr.io/$YOUR_PROJECT_ID/img-$YOUR_SERVICE

# DEPLOYMENT TO CloudRun
gcloud run deploy $YOUR_SERVICE \
    --image gcr.io/$YOUR_PROJECT_ID/img-$YOUR_SERVICE \
    --set-env-vars PSQL_IP=$PSQL_IP,PSQL_USER=$PSQL_USER,PSQL_PWD=$PSQL_PWD \
    --region $YOUR_REGION \
    --allow-unauthenticated \
    --network=$YOUR_NETWORK \
    --subnet=$YOUR_SUBNET 