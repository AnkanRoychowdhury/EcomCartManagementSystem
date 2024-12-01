package tech.ankanroychowdhury.ecomcartmanagementsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findCartByCartId(String cartId);
}
