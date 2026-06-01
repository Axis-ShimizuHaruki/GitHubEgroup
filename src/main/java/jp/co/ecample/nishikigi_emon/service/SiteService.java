package jp.co.ecample.nishikigi_emon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.repository.SiteRepository;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    public Site findById(Integer siteId) {
        return siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("現場が存在しません"));
    }
    
 // DBから社員の情報を全件取得
 	public List<Site> selectAll() {
 		return siteRepository.findAll();
 	}
}