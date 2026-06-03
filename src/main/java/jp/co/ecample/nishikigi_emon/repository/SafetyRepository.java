package jp.co.ecample.nishikigi_emon.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Safety;

@Repository
public interface SafetyRepository extends JpaRepository<Safety, Integer> {
	@Query(value = """
			SELECT s
			FROM Safety s
			JOIN s.site st
			WHERE
			(:siteId IS NULL OR st.siteId = siteId)
			AND
			(:sCreatedAt IS NULL OR DATE(s.sCreatedAt) = :sCreatedAt)
			AND(
			    :judgement IS NULL
			    OR :judgement = ''
			    OR (
			        :judgement = '良好'
			        AND (
			            s.scaffolding +
			            s.protectingOpenings +
			            s.safetyHarness +
			            s.equipmentInspection +
			            s.fireExtinguisher +
			            s.organization +
			            s.electricalInsulation
			        ) = 0
			    )
			    OR (
			        :judgement = '要対応'
			        AND (
			            s.scaffolding +
			            s.protectingOpenings +
			            s.safetyHarness +
			            s.equipmentInspection +
			            s.fireExtinguisher +
			            s.organization +
			            s.electricalInsulation
			        ) > 0
			    )
			)
			""")
	List<Safety> search(
			@Param("sCreatedAt") LocalDate sCreatedAt,
			@Param("siteId") Integer siteId,
			@Param("judgement") String judgement);
}
