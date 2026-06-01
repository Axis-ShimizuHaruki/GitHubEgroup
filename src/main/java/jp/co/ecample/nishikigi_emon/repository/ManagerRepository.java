package jp.co.ecample.nishikigi_emon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Manager;


@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
	Optional<Manager> findByUser_Userid(Integer userid);

}