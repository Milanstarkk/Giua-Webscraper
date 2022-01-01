/*
 * Giua Webscraper library
 * A webscraper of the online school workbook giua@school
 * Copyright (C) 2021 - 2022 Hiem, Franck1421 and contributors
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

package com.giua.objects.interviews;

public class SentInterview {
    public final String teacher;
    public final String subject;
    public final String dateAndHour;
    public final String state;
    public final String cancelLink;

    public SentInterview(String teacher, String subject, String dateAndHour, String state, String cancelLink) {
        this.teacher = teacher;
        this.subject = subject;
        this.dateAndHour = dateAndHour;
        this.state = state;
        this.cancelLink = cancelLink;
    }

    public boolean isCanceled() {
        return state.equals("Appuntamento annullato dal genitore");
    }

    public String toString() {
        return "Docente: " + teacher + " ; Materia: " + subject + " ; Data e ora: " + dateAndHour + " ; Stato della richiesta: " + state;
    }

}
