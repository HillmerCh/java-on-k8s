# Deploying  Java Applications with Kubernetes 

This repository shows how to use Docker and Kubernetes with Java Applications. 

Requirements 

* Docker: https://www.docker.com/products/docker-desktop
* Maven: https://maven.apache.org
* Kubectl: https://kubernetes.io/docs/tasks/tools/install-kubectl/
* Oracle Kubernetes Engine OKE: https://cloud.oracle.com/home
* Minikube (Optional) https://kubernetes.io/docs/tasks/tools/install-minikube/ 

## The Java Store Application


The java-store is the basic Java application used throughout the Docker and Kubertenes demos. It has two modules: a simple RESTFul Service and a Web Client. It uses Maven and Java Technologies such as SpringBoot, JAX-RS, CDI, JPA, PrimeFaces and Bean Validation.

You can use any Maven capable IDE such as NetBeans. We use Postgres but you can use any relational database such as MySQL.

The application is composed of:

- **A RESTFul service*:** protocol://hostname:port/store/product/v1

	- **_GET by Id_**: protocol://hostname:port/javaee-cafe/rest/coffees/{id} 
	- **_GET all_**: protocol://hostname:port/store/product/v1
	- **_POST_** to add a new element at: protocol://hostname:port/store/product/v1/
	- **_DELETE_** to delete an element at: protocol://hostname:port/store/product/v1/{id}

- **A Web Client:** protocol://hostname:port/store-web-client/

## Docker

* Open a console. Add maven to PATH:
	```
	export PATH=<Your Maven directorty>/bin/:$PATH 
	```
	
* Navigate to where you have this repository code in your file system. 

###  The RESTFul Service 


####  Creating the Docker image

* Compile and Package the RESTFul application via maven:
	```
	mvn package -f store-rest-api/pom.xml 
	```

* Copy the .jar file into docker directory:
	```
	cp store-rest-api/target/store-rest-api.jar docker/rest-api
	```

* Make sure Docker is running. Build a Docker image tagged `store-rest-api:v1` issuing the command:
	```
	docker build -t store-rest-api:v1 docker/rest-api/.
	```

####  Deploying the RESTFul Service with Docker

* Open a new console.

* Enter the following command and wait for the database to come up fully.
	```
	docker run -it --rm --name store-db -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres
	```

* Wait for Postgres database is ready to accept connections (to stop the database, simply press Control-C).

* Open a new console.
 
* To run the newly built images, use the command:
	```
	docker run -it --rm -e POSTGRES_HOST="docker.for.mac.localhost" -e POSTGRES_USER="postgres" -p 8080:8080 store-rest-api:v1
	```

	> **Note:** If your Docker instance is running on Linux/Windows before to build the Docker image it is necessary to replace the POSTGRES_HOST environment variable to POSTGRES_HOST from "docker.for.mac.localhost" to serverName="localhost"

	
* Wait for Open Liberty to start and the RESTFul Service to deploy sucessfully (to stop the service and Liberty, simply press Control-C).

* Once the RESTFul Service starts, you can test the service at the URL: [http://localhost:8080/store/product/v1/](http://localhost:8080/store/product/v1/).



###  The Web Client 

####  Creating the Docker image

* Compile and Package the Web Client via maven:
	```
	mvn package -f store-web-client/pom.xml -DskipTests=true
	```

* Copy the .war file into docker directory:
	```
	cp store-web-client/target/store-web-client.war docker/web-client
	```

* Make sure Docker is running. Build a Docker image tagged `store-web-client:v1` issuing the command:
	```
	docker build -t store-web-client:v1 docker/web-client/.
	```

####  Deploying the Web Client with Docker

* To run the newly built images, use the command:
	```
	docker run -it --rm -e JAVA_STORE_API_URI='http://<your local ip>:8080/store/product/v1' -p 9080:9080 store-web-client:v1
	```
	
* Wait for Open Liberty to start and the Web Client to deploy sucessfully (to stop the application and Liberty, simply press Control-C).

* Once the application starts, you can test the REST service via the Web Client at [http://localhost:9080/store-web-client/](http://localhost:9080/store-web-client/).


## Kubernetes

This demo uses the service of Oracle Cloud OKE as  Kubernetes Engine to deploy the application, OKE has been configured to be accessed through the console. Docker hub (https://hub.docker.com/) service is used as Docker images repository. 

You can use minikube to run the demo locally, [go to the instructions to run the demo with Minukube](MINIKUBE.md)

### Pushing the Docker image to Docker hub

* Make sure that .jar and .war files are created and copied as described in the step `Creating the Docker image` :
	```
	ls -l docker/rest-api
	
	ls -l docker/web-client
	```

* Log in to Docker Hub using the docker login command:
   ```
   docker login
   ```
* Build the Docker images and push them to Docker Hub:
   ```
   docker build -t <your Docker Hub account>/store-rest-api:<your docker image version> docker/rest-api/.
      
   docker push <your Docker Hub account>/store-rest-api:<your docker image version>
     
      
   docker build -t <your Docker Hub account>/store-web-client:<your docker image version> docker/web-client/.
   
   docker push <your Docker Hub account>/store-web-client:<your docker image version>
   ```

### Deploying the Application on Oracle Kubernetes Engine
  
* Open a terminal. 

* List the containers running in a Cluster:
   ```
   kubectl get pods
   ```
   
* Start the proxy to the Kubernetes API server:
   ```
   kubectl proxy
   ```
   
* Once the Oracle Kubernetes Engine Console starts, you can login at the URL: [http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login](http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login) 
   
* Deploy the Postgres database with a persistent volume claim with the following command:
   ```
   kubectl create -f kubernetes/store-db.yml
   ```

* Get the pod for Postgres database:
   ```
   kubectl get pods
   ```
   
* Connect to database:
   ```
   kubectl exec -it store-db-<POD> -- psql -U postgres
   ```

* On Postgres and run the command `\dt` to see the tables, '\q' to logout Postgres
  
   
* Create a config map with the hostname of Postgres database:
   ```
   kubectl create configmap store-db-hostname-config --from-literal postgres_host=$(kubectl get svc store-db -o jsonpath="{.spec.clusterIP}")
   ``` 
   
* Deploy the RESTFul service. Replace the `<your Docker Hub account>` value with your account name in `kubernetes/store-rest-api.yml` file, then deploy the application:
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
   kubectl get svc store-rest-api
   ```
   > **Note:** It may take a few minutes for the load balancer to be created.

* Deploy the Web Client. Replace the `<your Docker Hub account>` value with your account name in `kubernetes/store-web-client.yml` file, then deploy the application:
   ```
   kubectl create -f kubernetes/store-web-client.yml
   ```

* Check the logs:
   ```
   kubectl logs store-web-client-<POD>
   ```
   
* Get the External IP address of the Web Client Service, then the application will be accessible at `http://<External IP Address>:<Port>/store-web-client/`:
   ```
   kubectl get svc store-web-client
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