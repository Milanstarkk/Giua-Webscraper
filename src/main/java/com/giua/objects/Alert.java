/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2021 Hiem, Franck1421 and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package com.giua.objects;

import com.giua.webscraper.GiuaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Vector;

public class Alert {
    public final String status;
    public final String date;
    public final String receivers;
    public final String object;
    public final int page;  //La pagina in cui si trova questo avviso
    public final String detailsUrl;
    public String details;
    public String creator;
    public String alertType;
    public List<String> attachmentUrls;
    public boolean isDetailed;  //Indica se per questo avviso sono stati caricati i dettagli

    public Alert(String status, String date, String receivers, String object, String detailsUrl, int page) {
        this.status = status;
        this.date = date;
        this.receivers = receivers;
        this.object = object;
        this.page = page;
        this.detailsUrl = detailsUrl;
        this.isDetailed = false;
    }

    /**
     * Ottiene i dettagli, il tipo e il creatore dell'avviso con una richiesta HTTP
     *
     * @param gS
     * @return Una Stringa contenente i dettagli dell'avviso
     */
    public String getDetails(GiuaScraper gS) {
        if (!this.isDetailed) {
            Document detailsHTML = gS.getPage(detailsUrl);
            this.attachmentUrls = new Vector<>();
            this.details = detailsHTML.getElementsByClass("gs-text-normal").get(0).text();
            this.creator = detailsHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
            Elements els = detailsHTML.getElementsByClass("gs-mt-2");
            if (els.size() == 3)
                this.alertType = detailsHTML.getElementsByClass("gs-mt-2").get(2).text().split(": ")[1];
            else
                this.alertType = detailsHTML.getElementsByClass("gs-mt-2").get(1).text().split(": ")[1];

            Elements attachmentsHTML = detailsHTML.getElementsByClass("gs-ml-3");
            for (Element attachmentHTML : attachmentsHTML)
                this.attachmentUrls.add(attachmentHTML.attr("href"));

            this.isDetailed = true;
        }
        return this.details;
    }

    public boolean isRead() {
        return this.status.equals("LETTO");
    }

    public String toString() {
        if (!this.isDetailed)
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.object;
        else
            return this.status + "; " + this.date + "; " + this.receivers + "; " + this.object + "; " + this.creator + "; " + this.details + "; " + this.alertType;
    }
}
