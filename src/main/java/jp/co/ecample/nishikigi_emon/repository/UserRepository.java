package jp.co.ecample.nishikigi_emon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
