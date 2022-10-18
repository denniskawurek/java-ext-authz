# Envoy ext-authz in Java

Example JAVA implementation of the check method for the Enovy Proxy.

This orientates on the [Istio](https://github.com/istio/istio/tree/release-1.15/samples/extauthz) sample.

When you deploy it and the filter gets activated, then all requests with the `x-ext-authz: allow` header will pass.

Check my posts for a guide how to use it:

1. [Using Istio as an external authorizer. Part 1: The theory](https://blog.dkwr.de/development/istio-as-authorization-part-1/)
2. [Using Istio as an external authorizer. Part 2: The implementation
   ](https://blog.dkwr.de/development/istio-as-authorization-part-2/)
3. [Implementation of an external authorizer for Istio in Java
   ](https://blog.dkwr.de/development/ext-authz-java/)

## Usage

1. Clone this repo.
2. In a terminal go into the directory.
3. Build the jar: `gradle build`
4. Build a Docker image: `docker build -t java-authz:v1 .`
5. Deploy into your Kubernetes cluster

Now you can test the filter for example with cURL.
Requests with the `-H "x-ext-authz: allow"` header should pass. Other ones fail.

E.g.:

```
curl -H "x-ext-authz: allow" localhost/books
```

### Addendum: Kubernetes Resource files

Here is a K8S resource file for the namespace `sidecar-test`:

```yml
apiVersion: v1
kind: Service
metadata:
  name: ext-authz
  namespace: sidecar-test
  labels:
    app: ext-authz
spec:
  ports:
  - name: grpc
    port: 9000
    targetPort: 9000
  selector:
    app: ext-authz
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ext-authz
  namespace: sidecar-test
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ext-authz
  template:
    metadata:
      labels:
        app: ext-authz
    spec:
      containers:
      - image: java-authz:v1
        imagePullPolicy: IfNotPresent
        name: ext-authz
        ports:
        - containerPort: 9000
---
```