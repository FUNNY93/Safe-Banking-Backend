

package com.safebank.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.safebank.banking.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
		
	  boolean existsByMobile(String mobile);

	    boolean existsByEmail(String email);

	    boolean existsByAadhar(String aadhar);

	    boolean existsByPan(String pan);
	    User findByMobile(String mobile);
	    User findByEmail(String email);
	    User findByAccountNumber(String accountNumber);
	    
}
