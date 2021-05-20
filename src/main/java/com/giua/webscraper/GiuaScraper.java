package com.giua.webscraper;

import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* -- Giua Webscraper alpha 0.6.x -- */
// Tested with version 1.2.x and 1.3.0 of giua@school
public class GiuaScraper extends GiuaScraperExceptions
{

	public static class Alerts {
		public final String status;
		public final String date;
		public final String receivers;
		public final String objectAvviso;
		public String details;
		public String creator;
		public final int page;
		public final int id;        //Indica quanto e' lontano dal primo avviso partendo dall'alto
		public boolean isDetailed;

		Alerts(String status, String data, String destinatari, String oggetto, int id, int page) {
			this.status = status;
			this.date = data;
			this.receivers = destinatari;
			this.objectAvviso = oggetto;
			this.id = id;
			this.page = page;
			this.isDetailed = false;
		}

		public String getDetails() {        //carica i dettagli e l'autore dell'avviso simulando il click su Visualizza
			Document allAvvisiHTML = getPage(SiteURL + "/genitori/avvisi/" + page);
			Document dettagliAvvisoHTML = getPage(SiteURL + "" + allAvvisiHTML.getElementsByClass("label label-default").get(this.id).parent().parent().child(4).child(0).attributes().get("data-href"));
			this.details = dettagliAvvisoHTML.getElementsByClass("gs-text-normal").get(0).text();
			this.creator = dettagliAvvisoHTML.getElementsByClass("text-right gs-text-normal").get(0).text();
			this.isDetailed = true;
			return this.details;
		}

		public boolean isRead() {
			return this.status.equals("LETTA");
		}

		//Ritorna una lista di Avvisi con tutti i loro componenti ma senza i dettagli
		public static List<Alerts> getAllAvvisi(int page) {
			if(page < 0){throw new IndexOutOfBoundsException("Un indice di pagina non puo essere 0 o negativo");}
			List<Alerts> allAvvisi = new Vector<Alerts>();
			Document doc = getPage(SiteURL + "/genitori/avvisi/" + page);
			Elements allAvvisiLettiStatusHTML = doc.getElementsByClass("label label-default");
			Elements allAvvisiDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

			int i = 0;
			for (Element el : allAvvisiLettiStatusHTML) {
				allAvvisi.add(new Alerts(el.text(),
						el.parent().parent().child(1).text(),
						el.parent().parent().child(2).text(),
						el.parent().parent().child(3).text(),
						i,
						page
				));
				i++;
			}
			for (Element el : allAvvisiDaLeggereStatusHTML) {
				allAvvisi.add(new Alerts(el.text(),
						el.parent().parent().child(1).text(),
						el.parent().parent().child(2).text(),
						el.parent().parent().child(3).text(),
						i,
						page
				));
				i++;
			}

			return allAvvisi;
		}

		public String toString() {
			if (!this.isDetailed)
				return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso;
			else
				return this.status + "; " + this.date + "; " + this.receivers + "; " + this.objectAvviso + "; " + this.creator + "; " + this.details;
		}
	}

	public static class Newsletters {
		public final String status;
		public final String date;
		public final String newslettersObject;
		public final String detailsUrl;
		public final String number;
		public final int page;
		public final int id;		//Indica quanto e' lontano dalla prima circolare

		Newsletters(String status, String numebr, String date, String newslettersObject, String detailsUrl, int id, int page) {
			this.status = status;
			this.date = date;
			this.newslettersObject = newslettersObject;
			this.detailsUrl = detailsUrl;
			this.number = numebr;
			this.id = id;
			this.page = page;
		}

		public boolean isRead(){
			return this.status.equals("LETTA");
		}

		//Ritorna una lista di Newsletters con tutti i loro componenti di una determinata pagina
		public static List<Newsletters> getAllNewsletters(int page) {
			List<Newsletters> allCirculars = new Vector<Newsletters>();
			Document doc = getPage(SiteURL + "/circolari/genitori/" + page);
			Elements allNewslettersLettiStatusHTML = doc.getElementsByClass("label label-default");
			Elements allNewslettersDaLeggereStatusHTML = doc.getElementsByClass("label label-warning");

			int i = 0;
			for (Element el : allNewslettersLettiStatusHTML) {
				allCirculars.add(new Newsletters(el.text(),
						el.parent().parent().child(1).text(),
						el.parent().parent().child(2).text(),
						el.parent().parent().child(3).text(),
						SiteURL + "" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
						i,
						page
				));
				i++;
			}
			for (Element el : allNewslettersDaLeggereStatusHTML) {
				allCirculars.add(new Newsletters(el.text(),
						el.parent().parent().child(1).text(),
						el.parent().parent().child(2).text(),
						el.parent().parent().child(3).text(),
						SiteURL + "" + el.parent().parent().child(4).child(1).child(0).child(0).child(0).getElementsByClass("btn btn-xs btn-primary gs-ml-3").get(0).attr("href"),
						i,
						page
				));
				i++;
			}

			return allCirculars;
		}

		public String toString(){
			return this.status + "; " + this.number + "; " + this.date + "; " + this.newslettersObject + "; " + this.detailsUrl;
		}
	}

	public static class Homework{
		public String day;		//usato per trovare quale compito interessa
		public String date;
		public String subject;
		public String creator;
		public String details;

		public Homework(String day, String date, String subject, String creator, String details){
			this.day = day;
			this.date = date;
			this.subject = subject;
			this.creator = creator;
			this.details = details;
		}

		public String toString() {
			return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details;
		}

		public static List<Homework> getAllHomeworks(){
			List<Homework> allHomeworks = new Vector<>();
			Document doc = getPage(SiteURL + "/genitori/eventi");
			Elements homeworksHTML = doc.getElementsByClass("btn btn-xs btn-default gs-button-remote");
			for(Element homeworkHTML: homeworksHTML){
				Document detailsHTML = getPage(SiteURL + "" + homeworkHTML.attributes().get("data-href"));
				String subject = detailsHTML.getElementsByClass("gs-big").get(0).text();
				String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
				String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

				allHomeworks.add(new Homework(
						homeworkHTML.parent().parent().text(),
						homeworkHTML.attributes().get("data-href").split("/")[4],
						subject,
						creator,
						details
				));
			}

			return allHomeworks;
		}

		public static Homework EmptyHomework(String date){
			return new Homework(
					date.split("-")[2],
					date,
					"",
					"",
					"No compiti"
			);
		}

		//Restituisce il compito di una determinata data. Data deve essere cosi: anno-mese-giorno
		public static Homework getHomework(String date){
			Document doc = getPage(SiteURL + "/genitori/eventi/dettagli/" + date + "/P");
			try {
				String subject = doc.getElementsByClass("gs-big").get(0).text();
				String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
				String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

				return new Homework(
						date.split("-")[2],
						date,
						subject,
						creator,
						details
				);
			} catch (NullPointerException e){		//Non ci sono compiti in questo giorno
				return EmptyHomework(date);
			}
		}
	}

	public static class Test{
		public final String day;		//usato per trovare quale verifica interessa
		public final String date;
		public final String subject;
		public final String creator;
		public final String details;
		public final boolean exists;

		public Test(String day, String date, String subject, String creator, String details, boolean exists){
			this.day = day;
			this.date = date;
			this.subject = subject;
			this.creator = creator;
			this.details = details;
			this.exists = exists;
		}

		public String toString() {
			return this.date + "; " + this.creator + "; " + this.subject + "; " + this.details + "; " + this.exists;
		}

		public static List<Test> getAllTestsWithoutDetails(){
			List<Test> allTests = new Vector<>();
			Document doc = getPage(SiteURL + "/genitori/eventi");
			Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
			for(Element testHTML: testsHTML){

				allTests.add(new Test(
						testHTML.parent().parent().text(),
						testHTML.attributes().get("data-href").split("/")[4],
						"",
						"",
						"",
						true
				));
			}

			return allTests;
		}

		public static List<Test> getAllTests(){		//Se ci sono molti elementi e quindi link potrebbe dare connection timed out.
													//Meglio utilizzare prima quello senza dettagli e poi andare a prendere la verifica singolarmente con getTest
			List<Test> allTests = new Vector<>();
			Document doc = getPage(SiteURL + "/genitori/eventi");
			Elements testsHTML = doc.getElementsByClass("btn btn-xs btn-primary gs-button-remote");
			for(Element testHTML: testsHTML){
				Document detailsHTML = getPage(SiteURL + "" + testHTML.attributes().get("data-href"));
				String subject = detailsHTML.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
				String creator = detailsHTML.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
				String details = detailsHTML.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

				allTests.add(new Test(
						testHTML.parent().parent().text(),
						testHTML.attributes().get("data-href").split("/")[4],
						subject,
						creator,
						details,
						true
				));
			}

			return allTests;
		}

		public static Test EmptyTest(String date){
			return new Test(
					date.split("-")[2],
					date,
					"",
					"",
					"No verifiche",
					false
			);
		}

		//Restituisce il compito di una determinata data. Data deve essere cosi: anno-mese-giorno
		public static Test getTest(String date){
			Document doc = getPage(SiteURL + "/genitori/eventi/dettagli/" + date + "/V");
			try {
				String subject = doc.getElementsByClass("gs-text-normal").get(0).text().split(": ")[1];
				String creator = doc.getElementsByClass("gs-text-normal").get(1).text().split(": ")[1];
				String details = doc.getElementsByClass("gs-text-normal gs-pt-3 gs-pb-3").get(0).text();

				return new Test(
						date.split("-")[2],
						date,
						subject,
						creator,
						details,
						true
				);
			} catch (IndexOutOfBoundsException e){		//Non ci sono verifiche in questo giorno
				return EmptyTest(date);
			}
		}
	}

	private static class Vote{
		public final String value;
		public final boolean isFirstQuarterly;
		public final boolean isAsterisk;
		public final String date;
		public final String judgement;
		public final String testType;
		public final String arguments;

		public Vote(String value, String date, String testType, String arguments, String judgement, boolean isFirstQuarterly, boolean isAsterisk){
			this.value = value;
			this.date = date;
			this.testType = testType;
			this.arguments = arguments;
			this.judgement = judgement;
			this.isFirstQuarterly = isFirstQuarterly;
			this.isAsterisk = isAsterisk;
		}

		//Deve essere usata solo da getAllVotes e serve a gestire quei voti che non hanno alcuni dettagli
		private static String getDetailOfVote(Element e, int index){
			try {
				return e.siblingElements().get(e.elementSiblingIndex()).child(0).child(0).child(index).text().split(": ")[1];
			} catch (Exception err){
				return "";
			}
		}

		//Ritorna una mappa fatta in questo modo: {"italiano": [tutti voti italiano], ...}
		public static Map<String, List<Vote>> getAllVotes() {

			Map<String, List<Vote>> returnVotes = new HashMap<>();
			Document doc = getPage(SiteURL + "/genitori/voti");
			Elements votesHTML = doc.getElementsByAttributeValue("title", "Informazioni sulla valutazione");

			for (final Element voteHTML : votesHTML) {
				final String voteAsString = voteHTML.text(); //prende il voto
				final String materiaName = voteHTML.parent().parent().child(0).text(); //prende il nome della materia
				final String voteDate = getDetailOfVote(voteHTML, 0);
				final String type = getDetailOfVote(voteHTML, 1);
				final String args = getDetailOfVote(voteHTML, 2);
				final String judg = getDetailOfVote(voteHTML, 3);
				final boolean isFirstQuart = voteHTML.parent().parent().parent().parent().getElementsByTag("caption").get(0).text().equals("Primo Quadrimestre");

				if (voteAsString.length() > 0) {    //Gli asterischi sono caratteri vuoti
					if (returnVotes.containsKey(materiaName)) {			//Se la materia esiste gia aggiungo solamente il voto
						List<Vote> tempList = returnVotes.get(materiaName); //uso questa variabile come appoggio per poter modificare la lista di voti di quella materia
						tempList.add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));
					} else {
						returnVotes.put(materiaName, new Vector<Vote>() {{
							add(new Vote(voteAsString, voteDate, type, args, judg, isFirstQuart, false));    //il voto lo aggiungo direttamente
						}});
					}
				} else {		//e' un asterisco
					if(returnVotes.containsKey(materiaName)){
						returnVotes.get(materiaName).add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
					} else {
						returnVotes.put(materiaName, new Vector<Vote>() {{
							add(new Vote("", voteDate, type, args, judg, isFirstQuart, true));
						}});
					}
				}
			}

			return returnVotes;
		}

		//Mette anche i dettagli nella stringa
		public String allToString() {
			if (this.isAsterisk) {
				return "*; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
			} else {
				return this.value + "; " + this.date + "; " + this.testType + "; " + this.arguments + "; " + this.judgement;
			}
		}

		public String toString(){
			return (this.isAsterisk) ? "*" : this.value;
		}
	}

	// Insert user and password of the account
	private static String user = "";

	public static void setUser(String u){
		user = u;
	}

	private static String password = "";

	public static void setPassword(String p){
		password = p;
	}

	//URL del registro
	private static final String SiteURL = "https://registro.giua.edu.it";

	private static Map<String, String> PHPSESSID = null; //TODO: modificare la variabile in modo che contenga solo il cookie che ci interessa e non tutti
	private static String CSRFToken = null;


	public static Document getPage(String url) {
		try {

			if(!checkLogin()) {
				print("getPage: Not logged in");
				print("getPage: Calling login method");
				login(user, password);
			}

			print("getPage: Getting page...");

			Connection.Response res = Jsoup.connect(url)
					.method(Method.GET)
					.cookies(PHPSESSID)
					.execute();

			Document doc = res.parse();

			print("getPage: Done!");
			return doc;


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Boolean checkLogin() {
		try {

			if(PHPSESSID == null) {
				return false; //Not logged in
			}

			Connection.Response res = Jsoup.connect(SiteURL + "/login/form/")
				    .method(Method.POST)
				    .cookies(PHPSESSID)
				    .execute();

			Document doc = res.parse();

			Elements logout_button = doc.getElementsByAttributeValue("title", "Esci dal Registro Elettronico");

			//The login screen is the most simple to scrape information from, in this case we search
			//for the log out button, if it doesn't exist we are not logged in.
			// Since we have already loaded the login page, this process is very fast
			return logout_button.size() > 0; //ritorna true se sei loggato altrimenti false


		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	//The most important function, it handles the login process
	public static void login(String username, String password)
	{
		try {

			//print("login: First connection (login form)");

			Connection.Response res = Jsoup.connect(SiteURL + "/login/form")
            	    .method(Method.GET)
            	    .execute();

            	//Document doc = res.parse();
            	PHPSESSID = res.cookies();



			//print("login: Second connection (authenticate)");
			Connection.Response res2 = Jsoup.connect(SiteURL + "/ajax/token/authenticate")
					.cookies(PHPSESSID)
					.method(Method.GET)
					.ignoreContentType(true)
					.execute();

			print("login: get csrf token");


			String csrfString = res2.body().split("\":\"")[1];
			CSRFToken = csrfString.substring(0, csrfString.length()-2);		//prende solo il valore del csrf

			//print("Page content: " + res2.body());
			print("login: CSRF Token: " + CSRFToken);

			//print("login: Third connection (login form)");
			Connection.Response res3 = Jsoup.connect(SiteURL + "/login/form/")
					.data("_username", username, "_password", password, "_csrf_token", CSRFToken, "login", "")
					.cookies(PHPSESSID)
					.method(Method.POST)
					.execute();

			PHPSESSID = res3.cookies();
			System.out.printf("login: Cookie: %s\n", PHPSESSID);

			Document doc2 = res3.parse();


			if(PHPSESSID.isEmpty()){
				Elements err = doc2.getElementsByClass("alert alert-danger"); //prendi errore dal sito
				throw new SessionCookieEmpty("Session cookie empty, login unsuccessful. Site says: " + err.text());
			}

			print("login: Logged in as " + username + " with account type " + getUserType(doc2));


			//print("HTML: " + doc2);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void print(String string) {
		System.out.println(string);
		//WHY THE FUCK IS THE PRINTING FUCTION SO LONG???
	}


	public static String getUserType(Document doc){
		//final Document doc = getPage(SiteURL + "/");
		//TODO: Forse è un pò troppo eccessivo caricare una pagina ogni volta che si vuole il tipo di account
		//TODO: quindi per ora lo lascio commentato
		final Elements elm = doc.getElementsByClass("col-sm-5 col-xs-8 text-right");
		String text = elm.get(0).getElementsByTag("a").text();
		final String[] text2 = text.split("\\(");
		text = text2[1].replaceAll("\\)", "");
		return text;
	}


	//Main function, only used on the console version for testing
	public static void main(String[] args) {



		Scanner sc= new Scanner(System.in);
		if(user.equals("") && password.equals("")){
			print("Please enter username: ");
			user= sc.nextLine();
			print("Password: ");
			password= sc.nextLine();
		}

		print("\n-------------------\nConnecting to " + SiteURL + "\n-------------------\n");


		print("--------VOTI--------");

		print("Get votes");
		Map<String, List<Vote>> votes = Vote.getAllVotes();
		for(String m: votes.keySet()){
			print(m + ": " + votes.get(m).toString());
		}
		print(votes.get("Ed. civica").get(0).allToString());
		print(votes.get("Ed. civica").get(1).allToString());

		/*print("--------AVVISI---------");

		print("Get avvisi");
		List<Alerts> allAvvisi = Alerts.getAllAvvisi(1);
		for(Alerts a: allAvvisi){
			print(a.toString());
		}

		print("--------COMPITI--------");
		print("Get homeworks");
		List<Homework> allHomework = Homework.getAllHomeworks();
		for(Homework a: allHomework){
			print(a.toString());
		}
		print(Homework.getHomework("2021-05-28").toString());

		print("--------VERIFICHE--------");
		print("Get tests");
		List<Test> allTests = Test.getAllTestsWithoutDetails();
		for(Test a: allTests){
			print(a.toString());
		}
		print(Test.getTest("2021-05-18").toString());

		print("--------CIRCOLARI--------");
		print("Get tests");
		List<Newsletters> allNewsletters = Newsletters.getAllNewsletters(2);
		for(Newsletters a: allNewsletters){
			print(a.toString());
		}*/

	}
}