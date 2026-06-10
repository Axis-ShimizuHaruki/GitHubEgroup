package jp.co.ecample.nishikigi_emon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Site;


@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
	List<Site>  findBySiteId(Integer siteId);
}