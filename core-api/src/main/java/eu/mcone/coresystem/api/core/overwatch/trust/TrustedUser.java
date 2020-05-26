/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.trust;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.io.Serializable;

@Getter
@Setter
public class TrustedUser implements Serializable {

    private int correctReports;
    private int failReports;
    private TrustGroup group;

    public TrustedUser() {
        group = TrustGroup.NORMAL;
    }

    public TrustedUser(Document document) {
        correctReports = document.getInteger("correctReports");
        failReports = document.getInteger("failReports");
        group = (!document.getString("group").isEmpty() ? TrustGroup.valueOf(document.getString("group")) : TrustGroup.NORMAL);
    }

    public TrustedUser(int correctReports, int failReports, TrustGroup group) {
        this.correctReports = correctReports;
        this.failReports = failReports;
        this.group = group;
    }

    public int getReports() {
        return correctReports + failReports;
    }
}