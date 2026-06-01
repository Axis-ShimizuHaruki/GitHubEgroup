package jp.co.ecample.nishikigi_emon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.ecample.nishikigi_emon.entity.Trouble;

public interface TroubleRepository extends JpaRepository<Trouble, Integer>{

}
