## Kubernetes

### Creating the application Docker images into Minikube

* To work with the Docker daemon on your Mac/Linux host, use the docker-env command in your shell:
	```
	eval $(minikube docker-env)
	```

* Build the RESTFul Service Docker image:
   ```
   docker build -t store-rest-api:v1 docker/sb-api/.
   ```
   
* Build the Web Client Docker image:
   ```
   docker build -t store-web-client:v1 docker/jsf-client/.
   ```   
   	
### Deploying the Application on Minikube Engine
  
* Open a terminal. 

* List the containers running in a Cluster:
   ```
   kubectl get pods
   ```
   
* Start the proxy to the Kubernetes API server:
   ```
   kubectl proxy
   ```
* Deploy the Postgres database with a persistent volume claim with the following command:
   ```
   kubectl create -f kubernetes/store-db.yml
   ```

* Get the pod for Postgres database:
   ```
   kubectl get pods
   ```
   
* Connect to Postgres:
   ```
   kubectl exec -it store-db-<POD> -- psql -U postgres
   ```

* On Postgres and run the command `\dt` to see the tables, '\q' to logout Postgres
  
   
* Create a config map with the hostname of Postgres database:
   ```
   kubectl create configmap store-db-hostname-config --from-literal postgres_host=$(kubectl get svc store-db -o jsonpath="{.spec.clusterIP}")
   ``` 

* Deploy the RESTFul service. Remove the `<your Docker Hub account>` in `kubernetes/store-rest-api.yml` file, then deploy the application:

   ```
   kubectl create -f kubernetes/store-rest-api.yml
   ```

* Check the logs:
   ```
   kubectl logs store-rest-api-<POD>
   ```
   
* Create a config map with the hostname of the RESTFul service:
   ```
   kubectl create configmap store-rest-api-hostname-config --from-literal JAVA_STORE_API_URI=http://$(kubectl get svc store-rest-api -o jsonpath="{.spec.clusterIP}"):8080/store/product/v1
   ```
   
* Get the External IP address of the RESTFul Service, then the application will be accessible at `http://<External IP Address>:<Port>/store/product/v1/`:
   ```
   minikube service store-rest-api --url
   ```
   
* Deploy the Web Client.  Remove the `<your Docker Hub account>` comment in `kubernetes/store-web-client.yml` file, then deploy the application
   ```
   kubectl create -f kubernetes/store-web-client.yml
   ```

* Check the logs:
   ```
   kubectl logs store-web-client-<POD>
   ```
   
* Get the External IP address of the Web Client Service, then the application will be accessible at `http://<External IP Address>:<Port>/store-web-client/`:
   ```
   minikube service store-web-client --url
   ```

* Scale your RESTFul service:
   ```
   kubectl scale deployment store-rest-api --replicas=3
   ```
      
## Deleting the Resources


* Delete the Web Client deployment:
   ```
   kubectl delete -f kubernetes/store-web-client.yml
   ```

* Delete the RESTFul service hostname config map:
   ```
   kubectl delete cm store-rest-api-hostname-config
   ```

* Delete the RESTFul service deployment:
   ```
   kubectl delete -f kubernetes/store-rest-api.yml
   ```

* Delete the Postgres database hostname config map:
   ```
   kubectl delete cm store-db-hostname-config
   ```

* Delete Postgres database:
   ```
   kubectl delete -f kubernetes/store-db.yml
   ```