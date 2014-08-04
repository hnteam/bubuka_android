package ru.espepe.bubuka.player.pojo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wolong on 28/07/14.
 */
public class SyncListFiles extends PojoObject {
    private List<Domain> domains;
    private List<FileObject> objects;

    public SyncListFiles(Document doc) {
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

        this.objects = new ArrayList<FileObject>();

        for(Element fileObjectElement : doc.select("object")) {
            this.objects.add(new FileObject(fileObjectElement));
        }
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public List<FileObject> getObjects() {
        return objects;
    }
}
