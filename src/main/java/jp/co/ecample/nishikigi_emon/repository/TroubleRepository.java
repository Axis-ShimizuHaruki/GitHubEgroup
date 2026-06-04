package jp.co.ecample.nishikigi_emon.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.ecample.nishikigi_emon.entity.Trouble;

public interface TroubleRepository extends JpaRepository<Trouble, Integer> {

	@Query("""
			    SELECT t
			    FROM Trouble t
			    WHERE
			    (:occurredDate IS NULL
			        OR FUNCTION('DATE', t.occurredAt) = :occurredDate)

			    AND (:siteName IS NULL OR t.site.siteName LIKE %:siteName%)

			    AND (:priority IS NULL OR t.priority = :priority)

			    AND (:troubleType IS NULL OR t.troubleType = :troubleType)

			    AND (:statusFlag IS NULL OR t.tStatusFlag = :statusFlag)

			    ORDER BY t.occurredAt DESC
			""")
	List<Trouble> search(
			LocalDate occurredDate,
			String siteName,
			Integer priority,
			Integer troubleType,
			Integer statusFlag);

	 List<Trouble> findByTStatusFlagNot(Integer priority);
}
