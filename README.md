# Get Started Developing
EcomCartManagementSystem Microservice is intended to develop & maintain the cart management facility for an ecommerce. 

## Let's try it out

#### Before you start
- Install Java [At least version 11]
- Clone the repo: 
    ```bash 
    git clone https://github.com/AnkanRoychowdhury/EcomCartManagementSystem.git
    ```
- Change environment variable values in `src/main/resources/application.yml` file for more security or leave it as it is.
- Build the project: 
    ```bash 
    ./mvnw package -DskipTests
    ```
- Run the project: 
    ```bash
    java --jar path/to/jar-package-name.jar
    ```

#### Important endpoints
- http://localhost:8282 - Server URL [Make sure to change the port by default it's `8282`]
- http://localhost:8282/swagger-ui/index.html - Swagger Documentation
- https://ecomcartjavadoc.netlify.app - Cart Service Java Documentation to look upon package, class hierarchy details

### API Endpoints
Contains general input logic and validation: carts/cart items, guest cart manage api contracts.

| Method	 | Path	                                        | Description	                                                | User authentication	 | Available from UI |
|---------|----------------------------------------------|-------------------------------------------------------------|:--------------------:|:-----------------:|
| GET	    | /api/v1/carts?cartId={cartId}	               | Get specified cart data	                                    |          ✓           |         ✓         |
| POST	   | /api/v1/carts	                               | Create a cart with the given data in payload	               |          ×           |         ✓         |
| PUT	    | /api/v1/carts?cartId={cartId}                | Update the cart with the given data in payload	             |          ×           |        	✓         |
| PATCH	  | /api/v1/carts/items?={cartId}	               | Update or add cart items of a specified cartId	             |          ×           |         ✓         |
| DELETE	 | /api/v1/carts?cartId={cartId}                | Soft Delete a cart                                          |          ✓           |         ×         |
| POST	   | /api/v1/carts/merge/{userId}?cartId={cartId} | Merge guest cart to the logged in user                      |          ✓           |         ✓         |
| GET	    | /api/v1/carts/guest?cartId={cartId}          | Get guest user cart data from redis by the specified cartId |          ×           |         ✓         |

### Detailed API Reference

1. #### Get specified cart data

    ```http
      GET /api/v1/carts?cartId={cartId}
    ```
    
    | Parameter | Type   | Description           |
    |:----------|:-------|:----------------------|
    | `cartId`  | `UUID` | **Required**. Cart ID |

2. #### Get guest user cart data from redis by the specified cartId

    ```http
      GET /api/v1/carts/guest?cartId={cartId}
    ```
    
    | Parameter | Type   | Description                 |
    |:----------|:-------|:----------------------------|
    | `cartId`  | `UUID` | **Required**. Guest Cart ID |

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.