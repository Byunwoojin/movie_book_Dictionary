import java.io.Serializable;

import javax.swing.ImageIcon;
// Item 클래스 정의
class Item implements Serializable{
	String title;
	String year="2020";
	ImageIcon icon;
	int rate;
	String plot;
	String comment;
	public Item(String title, String year, ImageIcon icon, int rate, String plot, String comment) {
		this.title = title;
		this.year = year;
		this.icon = icon;
		this.rate = rate;
		this.plot = plot;
		this.comment = comment;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getPlot() {
		return plot;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
// Item 클래스를 상속하는 subclass인 Movie클래스 정의
class Movie extends Item{
	String director;
	String actor;
	String genre="드라마";
	String grade="전체";
	public Movie(String title, String year, ImageIcon icon, int rate, String plot, String comment, String director,
			String actor, String genre, String grade) {
		super(title, year, icon, rate, plot, comment);
		this.director = director;
		this.actor = actor;
		this.genre = genre;
		this.grade = grade;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
//Item 클래스를 상속하는 subclass인 Book클래스 정의
class Book extends Item{
	String author;
	String publisher;
	public Book(String title, String year, ImageIcon icon, int rate, String plot, String comment, String author,
			String publisher) {
		super(title, year, icon, rate, plot, comment);
		this.author = author;
		this.publisher = publisher;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
		
	}

