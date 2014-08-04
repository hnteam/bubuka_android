package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wolong on 28/07/14.
 */
public class SyncList extends PojoObject {
    private SyncObject syncObject;
    private List<Domain> domains;

    public SyncList(Document doc) {
        syncObject = new SyncObject(doc.select("object").first());
        domains = new ArrayList<Domain>();
        for(Element domainElement : doc.select("domain")) {
            domains.add(new Domain(domainElement));
        }

        Collections.sort(domains, new Comparator<Domain>() {
            @Override
            public int compare(Domain lhs, Domain rhs) {
                return compare(lhs.getPriority(), rhs.getPriority());
            }

            private int compare(int lhs, int rhs) {
                return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
            }
        });
    }

    public SyncObject getSyncObject() {
        return syncObject;
    }

    public void setSyncObject(SyncObject syncObject) {
        this.syncObject = syncObject;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }


}
