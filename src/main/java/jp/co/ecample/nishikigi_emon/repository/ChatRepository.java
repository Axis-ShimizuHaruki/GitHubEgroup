package jp.co.ecample.nishikigi_emon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

	List<Chat> findBySiteSiteIdOrderByDateTimeAsc(
            Integer siteId);
}
