package jp.co.ecample.nishikigi_emon.dto;

import jp.co.ecample.nishikigi_emon.entity.Site;

public class SiteView {

    private Site site;

    private Integer maxPriority;

    public SiteView(Site site, Integer maxPriority) {
        this.site = site;
        this.maxPriority = maxPriority;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Integer getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(Integer maxPriority) {
        this.maxPriority = maxPriority;
    }
}