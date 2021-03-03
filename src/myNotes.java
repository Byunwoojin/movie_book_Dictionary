import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class myNotes extends JFrame{
	Container c;
	Boolean isMod=false;
	Boolean ismovieItem = false; Boolean isbookItem = false;
	JTextField m_title, m_director, m_actor,b_title, b_author, b_publisher;
	JComboBox<String> m_genre, m_grade, m_year, b_year;
	JSlider m_sliderRate, b_sliderRate;
	JTextField tf_image, tf_poster;
	JTextArea m_plot, m_comment, b_plot, b_comment;
	JDialog Dialog;
	JRadioButton rbMovie, rbBook, rbMovie2, rbBook2;
	JButton btnMod, btnAdd, btnDel;
	HashMap<String,Item> ItemDic;
	JPanel panelMovie, panelBook;
	ImageIcon selectedItem_icon;
	String search_topic="제목";
	JTextArea tf_plot, tf_comment;
	MyPanel mp;
	String title, year, director,actor,genre,grade;
	String author, publisher;
	Movie modMovie; Book modBook; // 항목 수정을 위해 필요한 변수
	int rate;
	JPanel image,plot, comment;
	String selectedItem;
	JList entire_list, movie_list, book_list, search_list;
	JList selectedList;
	Vector entire_v,movie_v,book_v,search_v;
	Thread th;
	JLabel timerLabel;
	
	public myNotes() {
		// HashMap 컬렉션 정의
		ItemDic = new HashMap<String,Item>(); // 제목과 Item(Movie/Book)을 기록하는 HashMap
		
		setTitle("JAVA 04분반 1912062 변우진");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		c = getContentPane();
		c.setLayout(new BorderLayout());
		createMenu(); // 메뉴 생성
		createNote(); // Note생성
		createInputDialog(); // 대화상자 생성
		setSize(805,770);
		setVisible(true);
		th.start(); // 스레드 시작
		
	}
	private void createMenu() {
		JMenuBar mb = new JMenuBar();
		JMenuItem[]menuItem1 = new JMenuItem[3];
		JMenuItem[]menuItem2 = new JMenuItem[1];
		
		String[] itemTitle1={"불러오기","저장하기","종료"};
		String[] itemTitle2 = {"버전 정보"};
		
		JMenu fileMenu = new JMenu("파일");
		JMenu helpMenu = new JMenu("도움말");
		
		MenuActionListener listener = new MenuActionListener();
		// 파일 메뉴에, 3개의 메뉴아이템을 붙인다.
		for(int i=0; i<menuItem1.length; i++) {
			menuItem1[i] = new JMenuItem(itemTitle1[i]);
			menuItem1[i].addActionListener(listener); // 메뉴아이템에 Action리스너 등록
			fileMenu.add(menuItem1[i]);			
			if(i==1)
				fileMenu.addSeparator(); // 분리선 삽입
		} 
		
		// 도움말 메뉴에, 1개의 메뉴아이템을 붙인다.
		for(int i=0;i<menuItem2.length;i++) {
			menuItem2[i]=new JMenuItem(itemTitle2[i]);
			menuItem2[i].addActionListener(listener); // 메뉴아이템에 Action리스너 등록
			helpMenu.add(menuItem2[i]);
		}
		
		mb.add(fileMenu);
		mb.add(helpMenu);
		setJMenuBar(mb);
	
	}
	class MenuActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			JFileChooser chooser = new JFileChooser(); // FileChooser객체 생성
			switch(cmd){
			case "종료":
				int result = JOptionPane.showConfirmDialog(null, "종료하시겠습니까?","종료 확인",JOptionPane.YES_NO_OPTION);
				if(result== JOptionPane.YES_OPTION) 
					System.exit(0); // 프로그램 종료
				break;
			case "버전 정보":
				JOptionPane.showMessageDialog(null, "MyNotes 시스템 v1.0입니다.", "Message", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "불러오기":
				int ret1 = chooser.showOpenDialog(null); // 열기 목적 다이얼로그 출력
				if(ret1==JFileChooser.APPROVE_OPTION) {  
					try {
						String pathName = chooser.getSelectedFile().getPath(); // 사용자가 선택한 파일 이름 알아내기
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathName)); 
						// 파일 내용을 불러와서 컬렉션 객체로 저장하도록 한다.
						ItemDic = (HashMap<String,Item>)ois.readObject(); 
						entire_v = (Vector<Item>)ois.readObject();
						movie_v = (Vector<Item>) ois.readObject();
						book_v = (Vector<Item>)ois.readObject();
						
						// 불러온 항목들을 다시 리스트에 보여준다.
						entire_list.setListData(entire_v); 
						movie_list.setListData(movie_v);
						book_list.setListData(book_v);
					
					}
					catch(IOException e1) {
						System.out.println("파일 출력 오류");
					} 
					catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} 
				}
				break;
			case "저장하기":
				int ret2 = chooser.showSaveDialog(null); // 저장 목적 다이얼로그 출력
				if(ret2==JFileChooser.APPROVE_OPTION) {
					try {
					String pathName = chooser.getSelectedFile().getPath(); // 사용자가 선택한 파일 이름 알아내기
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathName)); 
					
					//선택된 파일에 컬렉션을 저장한다.
					oos.writeObject(ItemDic);
					oos.writeObject(entire_v); 
					oos.writeObject(movie_v);
					oos.writeObject(book_v);
					oos.close();
					}
					catch(IOException e2){
						System.out.println("파일 입력 오류");
					}
				}
				break;
			}
		}	
		}
	// Runnable을 클래스로 구현
	class TimerRunnable implements Runnable{ 
		private JLabel timerLabel;
		public TimerRunnable(JLabel timerLabel) {
			this.timerLabel=timerLabel;
		}
		@Override
		public void run() { // 스레드 코드 작성
			int n=0;
			while(true) {
				Calendar cal = Calendar.getInstance();
				int year= cal.get(Calendar.YEAR);
				int month= cal.get(Calendar.MONTH)+1;
				int day= cal.get(Calendar.DAY_OF_MONTH);
				int hour= cal.get(Calendar.HOUR);
				int minute = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);
				timerLabel.setText(year + "년 " + month + "월 " +day+ "일 " +hour+ ":" +minute + ":" +second);
			
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					return;
				}
			}
			
		}
		
	}
	private void createNote() {
		// Top
		JPanel panelTop = new JPanel();
		panelTop.setLayout(new FlowLayout(FlowLayout.LEFT,200,0));
		JLabel p_title = new JLabel("My Notes"); // title
		p_title.setFont(new Font("Arial",Font.BOLD+Font.ITALIC,30));
		p_title.setForeground(new Color(0,0,128));
		
		timerLabel = new JLabel(); // 앱을 실행하는 동안 현재시간을 보여주는 라벨
		timerLabel.setFont(new Font("돋움",Font.PLAIN,15));
		TimerRunnable runnable = new TimerRunnable(timerLabel); 
		th = new Thread(runnable); // 스레드 생성
		panelTop.add(p_title);
		panelTop.add(timerLabel);
		
		// Center
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(null);
		
		// Center(1): 탭팬 구성
		JPanel tab = new JPanel();
		tab.setSize(205, 600);
		tab.setLocation(0,10);
		tab.setLayout(new GridLayout());
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		
		
		MyListListener mll = new MyListListener();
		entire_v = new Vector<Item>();  // ItemCollections클래스 정의: List에 Item을 공급하는 벡터
		movie_v = new Vector<Item>();
		book_v = new Vector<Item>();
		search_v = new Vector<Item>();
		
		//리스트 객체 생성
		entire_list = new JList<String>(entire_v);  // [전체] 탭 선택시 보여주는 리스트
		entire_list.addListSelectionListener(mll);
	
		movie_list = new JList<String>(movie_v);    // [영화] 탭 선택시 보여주는 리스트
		movie_list.addListSelectionListener(mll);
		
		book_list = new JList<String>(book_v);		// [도서] 탭 선택시 보여주는 리스트
		book_list.addListSelectionListener(mll);
		
		JPanel sr_Panel = new JPanel();				// [검색] 탭 선택시 보여주는 Panel
		sr_Panel.setLayout(new BorderLayout(5,5));
		JPanel search_Panel = new JPanel();			// [검색]탭(1)-상단, 검색창
		search_Panel.setLayout(new BorderLayout(5,5));
		String[] sr_type_info = {"제목","별점"};
		JComboBox<String>sr_type = new JComboBox<String>(sr_type_info);
		search_Panel.add(sr_type,BorderLayout.WEST);
		JTextField sr_input = new JTextField(10);
		search_Panel.add(sr_input,BorderLayout.CENTER);
		JButton sr_btn = new JButton("검색");
		sr_btn.setSize(5, 5);
		search_Panel.add(sr_btn,BorderLayout.EAST);
		sr_Panel.add(search_Panel,BorderLayout.NORTH);
		
		search_list = new JList<String>(search_v);	// [검색]탭(2)-하단, 검색결과 출력을 위한 리스트
		search_list.addListSelectionListener(mll);
		sr_Panel.add(search_list,BorderLayout.CENTER);
		
					
		// 이벤트 리스너: 검색 Panel의 콤보박스에서 선택된 내용을("제목/"별점"), search_topic에 저장한다.
		sr_type.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>cb = (JComboBox<String>)e.getSource();
				search_topic = ""+cb.getSelectedItem();
				
			}
			
		});
		// 이벤트 리스너: 검색 Panel에서 "검색"버튼이 눌리면, 검색내용을 출력한다.
		sr_btn.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				search_v.removeAllElements();
				search_list.setListData(search_v);
				tf_plot.setText(""); tf_comment.setText("");
				String sr_key= sr_input.getText();
				
				Iterator<String> it = entire_v.iterator();
				while(it.hasNext()) {
					String title = it.next();
					if(search_topic.equals("제목")) {		// [제목]으로 검색한 경우
					int sr_index = title.indexOf(sr_key); // 검색어가 포함된 제목들이 모두 검색된다.
					
					if(sr_index >-1) {
						search_v.add(title);
					
					
						search_list.setListData(search_v);
						image.repaint();
						}
					}
					if(search_topic.equals("별점")) {    // [별점]으로 검색한 경우
						Item item = ItemDic.get(title);  
						int rate = item.rate;
						
						if(Integer.parseInt(sr_key)<=rate) {  // 검색어 이상의 항목들이 모두 검색된다.
							search_v.add(title);
							
						
							search_list.setListData(search_v);
							image.repaint();
						}
					}
			
				}
				if(search_v.isEmpty()==true) { // 검색 결과가 없는 경우
					JOptionPane.showMessageDialog(null, "["+sr_key+"]"+" 검색 결과가 없습니다.", "Message",JOptionPane.INFORMATION_MESSAGE);
					
				}
		}});
		
		
		entire_list.setFixedCellWidth(55);
		movie_list.setFixedCellWidth(55);
		book_list.setFixedCellWidth(55);
		search_list.setFixedCellWidth(55);
		
		// 탭팬 구성
		pane.addTab("전체", entire_list);
		pane.addTab("영화", movie_list);
		pane.addTab("도서", book_list);
		pane.addTab("검색", sr_Panel);
		tab.add(pane);
		panelCenter.add(tab);
		
		// 이벤트 리스너: 탭을 선택하면, 해당 탭의 리스트에 선택되어 있는, Item을 <상세보기>화면에 띄운다.
		pane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				tf_plot.setText(""); tf_comment.setText("");
				if(pane.getSelectedComponent()==sr_Panel) {
					ismovieItem=false; isbookItem=false;
				}
				else {
				selectedList = (JList)pane.getSelectedComponent();
				selectedItem = (String)selectedList.getSelectedValue();
				Item item = ItemDic.get(selectedItem);
				
				if(item instanceof Movie){
					Movie movie = (Movie)item;
					selectedItem_icon = movie.icon;
					title = movie.title;
					year = movie.year;
					rate = movie.rate;
					grade = movie.grade;
					director = movie.director;
					actor = movie.actor;
					genre = movie.genre;
				
					tf_plot.setText(movie.plot);
					tf_comment.setText(movie.comment);
				
					ismovieItem = true;
				
				}
				if(item instanceof Book) {
					Book book = (Book)item;
					selectedItem_icon = book.icon;
					title = book.title;
					year = book.year;
					rate = book.rate;
					author = book.author;
					publisher = book.publisher;
					year = book.year;
				
					tf_plot.setText(book.plot);
					tf_comment.setText(book.comment);
				
				isbookItem = true;
				
			}
				}
				image.repaint();
			}
		});
		
		// Center(2): 상세 보기
		JPanel detail = new JPanel();
		detail.setLocation(210, 10); detail.setSize(570,600);
		detail.setLayout(null);
		Border detailtBorder = BorderFactory.createTitledBorder("상세 보기"); // 제목이 붙여진 경계를 생성
		detail.setBorder(detailtBorder);
		
		// 상세보기(1) 이미지 
		image = new JPanel();
		image.setLayout(new GridLayout(0,1));
		image.setLocation(10,20); image.setSize(540,250);
		mp = new MyPanel();
		image.add(mp);
		detail.add(image);
		
		// 상세보기(2) 줄거리
		plot = new JPanel();
		Border plotBorder = BorderFactory.createTitledBorder("줄거리"); // 제목이 붙여진 경계를 생성
		plot.setBorder(plotBorder);
		tf_plot = new JTextArea(6,52);
		plot.add(new JScrollPane(tf_plot));
		detail.add(plot);
		plot.setLocation(10,270);plot.setSize(550,150);
		
		// 상세보기(3) 감상평
		comment = new JPanel();
		Border commentBorder = BorderFactory.createTitledBorder("감상평"); // 제목이 붙여진 경계를 생성
		comment.setBorder(commentBorder);
		tf_comment = new JTextArea(6,52);
		comment.add(new JScrollPane(tf_comment));
		detail.add(comment);
		comment.setLocation(10,430); comment.setSize(550,150);
		
		panelCenter.add(detail,BorderLayout.CENTER);
		
		// Bottom
		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new FlowLayout());
		btnActionListener bal = new btnActionListener();
		// Bottom(1): 추가 버튼
		btnAdd = new JButton("추가");
		btnAdd.addActionListener(bal);
		// Bottom(2): 수정 버튼
		btnMod = new JButton("수정");
		btnMod.setSize(10,10); btnMod.setLocation(0, 750);
		btnMod.addActionListener(bal);
		// Bottom(3): 삭제 버튼
		btnDel = new JButton("삭제");	
		btnDel.setSize(10,10);btnDel.setLocation(0, 500);
		btnDel.addActionListener(bal);
		
		panelBottom.add(btnAdd);
		for(int i=0;i<20;i++) {  //버튼 사이의 간격을 만들기 위해 삽입된, 의미없는 JPanel
			panelBottom.add(new JPanel());
		}
		panelBottom.add(btnMod); panelBottom.add(btnDel);
		for(int i=0;i<10;i++) {  //버튼 사이의 간격을 만들기 위해 삽입된, 의미없는 JPanel
			panelBottom.add(new JPanel());
		}
		
		c.add(panelTop,BorderLayout.NORTH);
		c.add(panelCenter, BorderLayout.CENTER);
		c.add(panelBottom, BorderLayout.SOUTH);
	}
	// Center(2)상세보기의  (1)'이미지'패널에 삽입된 부분
	class MyPanel extends JPanel{
		public void paintComponent(Graphics g) {  // 리스트에서 선택된 항목의 종류에 따라, 각기 다른 '그리기'작업 수행
			super.paintComponent(g);
			
			if(ismovieItem==false && isbookItem == false) {
				g.drawLine(10, 20, 200, 220);
				g.drawLine(200, 20, 10, 220);
				g.drawString("이미지 없음",85, 125);
			}
	
			if(ismovieItem==true) {  // Movie가 선택된 경우
		
				Image img = selectedItem_icon.getImage();
				g.drawImage(img, 10, 10, 200, 230, this);
			
				g.drawString("제목          "+title, 240, 30);
				g.drawString("감독          "+director,240,60);
				g.drawString("배우          "+actor, 240, 90);
				g.drawString("장르          "+genre, 240, 120);
				g.drawString("등급          "+grade, 240, 150);
				g.drawString("개봉년도     "+year, 240, 180);
				g.drawString("별점           "+rate, 240, 210);
			ismovieItem = false;
		}
			if(isbookItem == true) {  // Book이 선택된 경우
				Image img = selectedItem_icon.getImage();
				g.drawImage(img, 10, 10, 200, 230, this);
				
				g.drawString("제목          "+title, 240, 30);
				g.drawString("저자          "+author,240,70);
				g.drawString("출판사        "+publisher, 240, 110);
				g.drawString("출판년도     "+year, 240, 150);
				g.drawString("별점           "+rate, 240, 190);
				isbookItem = false;
			}
	}
		
	}
	// 이벤트 리스너: 리스트에서 항목이 선택되면, 해당Item정보 가져오기
	class MyListListener implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedList = (JList)e.getSource();
			
			selectedItem = (String)selectedList.getSelectedValue();
			Item item = ItemDic.get(selectedItem);
			
			if(item instanceof Movie) {  // Movie가 선택된 경우
				Movie movie = (Movie)item;
			
				selectedItem_icon = movie.icon;
				title = movie.title;
				year = movie.year;
				rate = movie.rate;
				grade = movie.grade;
				director = movie.director;
				actor = movie.actor;
				genre = movie.genre;
				
				tf_plot.setText(movie.plot);
				tf_comment.setText(movie.comment);
				ismovieItem = true;
				
			}
			if(item instanceof Book) {   // Book이 선택된 경우
				Book book = (Book)item;
				selectedItem_icon = book.icon;
				title = book.title;
				year = book.year;
				rate = book.rate;
				author = book.author;
				publisher = book.publisher;
				year = book.year;
				tf_plot.setText(book.plot);
				tf_comment.setText(book.comment);
				isbookItem = true;
				
			}
			image.repaint();
		}
	}
	// 이벤트 리스너: [추가],[수정],[삭제] 버튼을 눌렀을 때 이벤트 처리
	class btnActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btnAdd) {  // [추가] 버튼을 누른 경우
				Dialog.setVisible(true);  // 입력 대화상자를 띄운다.
				
				rbMovie.setSelected(true);
				rbBook2.setSelected(true);
				panelMovie.setVisible(true);
				panelBook.setVisible(false);
			}
			
			if(e.getSource()==btnMod) {   // [수정] 버튼을 누른 경우
				
				Dialog.setVisible(true);  	// 수정을 위해, 대화상자가 띄워지게 한다.
				Item item = ItemDic.get(selectedItem);
				
				if(item instanceof Movie) {  // Movie 항목이 수정되는 경우
					modMovie = (Movie)item;
					m_title.setText(modMovie.title);
					m_director.setText(modMovie.director);
					m_actor.setText(modMovie.actor);
					m_genre.setSelectedItem(modMovie.genre);
					m_grade.setSelectedItem(modMovie.grade);
					m_year.setSelectedItem(modMovie.year);
					tf_poster.setText(""+modMovie.icon);
					m_sliderRate.setValue(modMovie.rate);
					m_plot.setText(modMovie.plot);
					m_comment.setText(modMovie.comment);
					rbMovie2.setSelected(true);
					panelMovie.setVisible(true);
					panelBook.setVisible(false);
					
					}
				if(item instanceof Book) {	// Book 항목이 수정되는 경우
					modBook = (Book)item;
				
					b_title.setText(modBook.title);
					b_author.setText(modBook.author);
					b_publisher.setText(modBook.publisher);
					b_year.setSelectedItem(modBook.year);
					tf_image.setText(""+modBook.icon);
					b_sliderRate.setValue(modBook.rate);
					b_plot.setText(modBook.plot);
					b_comment.setText(modBook.comment);
					rbBook.setSelected(true);
					panelMovie.setVisible(false);
					panelBook.setVisible(true);
					
				}
				isMod=true;
			}
		
			if(e.getSource()==btnDel) {     // [삭제] 버튼을 누른 경우
				Item item = ItemDic.get(selectedItem);
				
				if(item instanceof Movie) {  // Movie항목이 삭제되는 경우
					Movie delMovie = (Movie)item;
					// 삭제 확인 메시지를 띄우고, Yes버튼을 누르면 해당 항목을 컬렉션에서 삭제한다.
					int result = JOptionPane.showConfirmDialog(null, "정말 삭제하시겠습니까?","삭제 확인",JOptionPane.YES_NO_OPTION);
						if(result== JOptionPane.YES_OPTION) {
							ItemDic.remove(delMovie);
							entire_v.remove(delMovie.title);
							movie_v.remove(delMovie.title);
							search_v.remove(delMovie.title);
					}
				}
				if(item instanceof Book) {		// Book항목이 삭제되는 경우
					Book delBook = (Book)item;
					// 삭제 확인 메시지를 띄우고, Yes버튼을 누르면 해당 항목을 컬렉션에서 삭제한다.
					int result = JOptionPane.showConfirmDialog(null, "정말 삭제하시겠습니까?","삭제 확인",JOptionPane.YES_NO_OPTION);
					if(result== JOptionPane.YES_OPTION) {
						ItemDic.remove(delBook);
						entire_v.remove(delBook.title);
						book_v.remove(delBook.title);
						search_v.remove(delBook.title);
					}
				}
				// 삭제 된 이후에, 변경된 리스트 목록을 보여준다.
				entire_list.setListData(entire_v);
				movie_list.setListData(movie_v);
				book_list.setListData(book_v);
				search_list.setListData(search_v);
				tf_plot.setText("");tf_comment.setText("");
				image.repaint();
			}
		}
	}
	// 항목 추가, 수정을 위한 대화상자
	private void createInputDialog() {
		Dialog = new JDialog();
		Dialog.setSize(400,780);
		Dialog.setTitle("입력");
		MyActionListener mal = new MyActionListener();
		
		
		// 콤보박스에 아이템을 공급하는 배열
		String[]genre_info = {"드라마","멜로/로맨스","범죄","호러"};
		String[]grade_info = {"전쳬","12세 이상","15세 이상","청소년 관람불가"};
		String[]year_info = {"2020","2019","2018","2017","2016","2015"};
		String[] movieInfo = {"제목","감독","배우","장르","등급","개봉년도","포스터","별점","줄거리","","감상평",""};
		String[] bookInfo = {"제목","저자","출판사","출판년도","책이미지","별점","책소개","","감상평","","",""};
		
		//Dialog(1) panelMovie: 라디오 버튼에서 Movie가 선택될 때, Dialog에 나타나는 패널
		panelMovie = new JPanel();
		Border movieBorder = BorderFactory.createTitledBorder("영화 정보"); // 제목이 붙여진 경계를 생성
		panelMovie.setBorder(movieBorder);
		panelMovie.setSize(400,750);
		panelMovie.setLayout(new BorderLayout(5,5));
		
		// panelTop-(1) Top, 라디오 버튼
		JPanel panelTop = new JPanel();
		ButtonGroup type = new ButtonGroup(); // 버튼 그룹 객체 생성
		rbMovie = new JRadioButton("Movie",true);// Movie가 선택된 상태
		rbBook = new JRadioButton("Book");
		MyItemListener mil = new MyItemListener();
		rbMovie.addItemListener(mil); 
		rbBook.addItemListener(mil);
		type.add(rbMovie); type.add(rbBook); // 라디오 버튼 버튼 그룹에 등록
		panelTop.add(rbMovie);panelTop.add(rbBook);
		panelMovie.add(panelTop,BorderLayout.NORTH);
		
		// panelLabel-(2) West, 입력해야할 목록들의 주제("제목","장르...)
		JPanel panelLabel = new JPanel();
		panelLabel.setLayout(new GridLayout(0,1,5,0));
		for(int i=0;i<movieInfo.length;i++) {
			panelLabel.add(new JLabel(movieInfo[i]));
		}
		panelMovie.add(panelLabel,BorderLayout.WEST);
		
		// panelCenter-(3) Center, Movie기본정보 + 줄거리,감상평
		JPanel panelCenter = new JPanel();
		panelMovie.setVisible(true);
		JPanel panelMovieInfo = new JPanel();
		panelMovieInfo.setLayout(new GridLayout(0,1,10,10));
		m_title = new JTextField();
		m_director = new JTextField();
		m_actor = new JTextField();
		m_genre = new JComboBox<String>(genre_info);
		m_grade = new JComboBox<String>(grade_info);
		m_year = new JComboBox<String>(year_info);
		
		
		JPanel poster = new JPanel();    // poster 패널: 파일경로 + 이미지 파일 '불러오기'버튼
		tf_poster = new JTextField(15);
		JButton m_btnUpload = new JButton("불러오기");
		m_btnUpload.addActionListener(mal);
		m_btnUpload.setSize(10, 5);
		poster.add(tf_poster);poster.add(m_btnUpload);
		
		m_sliderRate = new JSlider(JSlider.HORIZONTAL,1,10,5);
		m_sliderRate.setPaintLabels(true);
		m_sliderRate.setPaintTicks(true);
		m_sliderRate.setMajorTickSpacing(1);
		
		panelMovieInfo.add(m_title);panelMovieInfo.add(m_director); panelMovieInfo.add(m_actor);
		panelMovieInfo.add(m_genre); panelMovieInfo.add(m_grade); panelMovieInfo.add(m_year);
		panelMovieInfo.add(poster); panelMovieInfo.add(m_sliderRate);
		panelCenter.add(panelMovieInfo,BorderLayout.CENTER);
		
		// panelMovieInfo2 - Movie의 줄거리 및 감상평 입력
		JPanel panelMovieInfo2 = new JPanel();
		panelMovieInfo2.setLayout(new GridLayout(0,1,5,5));
		m_plot = new JTextArea(5,25);
		m_comment = new JTextArea(5,25);
		panelMovieInfo2.add(new JScrollPane(m_plot));
		panelMovieInfo2.add(new JScrollPane(m_comment));
		panelCenter.add(panelMovieInfo2,BorderLayout.CENTER);
		panelMovie.add(panelCenter,BorderLayout.CENTER);
		
		// panelTop-(4) South, OK버튼
		JPanel SouthPanel = new JPanel();
		SouthPanel.setLayout(new FlowLayout());
		JButton m_ok = new JButton("OK");
		SouthPanel.add(m_ok);
		m_ok.addActionListener(mal);
		panelMovie.add(SouthPanel,BorderLayout.SOUTH);
	
		Dialog.add(panelMovie);
		
		
		
		// Dialog(2) panelBook: 라디오 버튼에서 Book이 선택될 때, Dialog에 나타나는 패널
		panelBook = new JPanel();
		panelBook.setVisible(false);
		Border bookBorder = BorderFactory.createTitledBorder("도서 정보"); // 제목이 붙여진 경계를 생성
		panelBook.setBorder(bookBorder);
		panelBook.setSize(400,550);
		panelBook.setLayout(new BorderLayout(5,5));
		
		// b_panelTop-(1) Top, 라디오 버튼
		JPanel b_panelTop = new JPanel();
		ButtonGroup type2 = new ButtonGroup(); // 버튼 그룹 객체 생성
		rbMovie2 = new JRadioButton("Movie");
		rbMovie2.addItemListener(mil); // 라디오 버튼에 Item리스너 등록
		rbBook2 = new JRadioButton("Book",true);
		rbBook2.addItemListener(mil); // 라디오 버튼에 Item리스너 등록
		type2.add(rbMovie2); type2.add(rbBook2); // 라디오 버튼 버튼 그룹에 등록
		b_panelTop.add(rbMovie2);b_panelTop.add(rbBook2);
		panelBook.add(b_panelTop,BorderLayout.NORTH);
		
		// b_panelLabel-(2) West, 입력해야할 목록들의 주제("제목","저자"...)
		JPanel b_panelLabel = new JPanel();
		b_panelLabel.setLayout(new GridLayout(0,1,5,0));
		for(int i=0;i<bookInfo.length;i++) {
			b_panelLabel.add(new JLabel(bookInfo[i]));
		}
		panelBook.add(b_panelLabel,BorderLayout.WEST);
		
		// panelCenter-(3) Center, Book기본정보 + 줄거리,감상평
		JPanel b_panelCenter = new JPanel();
		JPanel panelBookInfo = new JPanel();
		panelBookInfo.setLayout(new GridLayout(0,1,10,10));
		b_title = new JTextField();
		b_author = new JTextField();
		b_publisher = new JTextField();
		b_year = new JComboBox<String>(year_info);
		
		JPanel image = new JPanel();// image 패널: 파일경로 + 이미지 파일 '불러오기'버튼
		tf_image = new JTextField(15);
		JButton b_btnUpload = new JButton("불러오기");
		b_btnUpload.addActionListener(mal);
		m_btnUpload.setSize(10, 5);
		image.add(tf_image);image.add(b_btnUpload);
		
		b_sliderRate = new JSlider(JSlider.HORIZONTAL,1,10,5);
		b_sliderRate.setPaintLabels(true);
		b_sliderRate.setPaintTicks(true);
		b_sliderRate.setMajorTickSpacing(1);
		panelBookInfo.add(b_title);panelBookInfo.add(b_author); panelBookInfo.add(b_publisher);
		panelBookInfo.add(b_year); panelBookInfo.add(image); panelBookInfo.add(b_sliderRate);
		b_panelCenter.add(panelBookInfo,BorderLayout.CENTER);
		
		// panelMovieInfo2 - Book의 줄거리 및 감상평 입력
		JPanel panelBookInfo2 = new JPanel();
		panelBookInfo2.setLayout(new GridLayout(0,1,5,5));
		b_plot = new JTextArea(5,25);
		b_comment = new JTextArea(5,25);
		panelBookInfo2.add(new JScrollPane(b_plot));
		panelBookInfo2.add(new JScrollPane(b_comment));
		b_panelCenter.add(panelBookInfo2,BorderLayout.CENTER);
		panelBook.add(b_panelCenter,BorderLayout.CENTER);
		
		// panelTop-(4) South, OK버튼
		JPanel SouthPanel2 = new JPanel();
		SouthPanel2.setLayout(new FlowLayout());
		JButton b_ok = new JButton("OK");
		SouthPanel2.add(b_ok);
		panelBook.add(SouthPanel2,BorderLayout.SOUTH);
		b_ok.addActionListener(mal);
		
		Dialog.add(panelBook);
	}
	
	// 이벤트 리스너: 다이얼로그 내의 [불러오기],[OK]버튼의 이벤트 처리
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			JFileChooser chooser = new JFileChooser();
			switch(cmd) {
			case "불러오기":
				String filePath=null;
				int ret1 = chooser.showOpenDialog(null);  // 파일 열기 창이 띄워진다
				if(ret1 == JFileChooser.APPROVE_OPTION)   
					filePath = chooser.getSelectedFile().getPath();
				if(panelMovie.isVisible()==true)  // Dialog에 panelMovie가 나타난 경우
					tf_poster.setText(filePath);
				if(panelBook.isVisible()==true)   // Dialog에 panelBook이 나타난 경우
					tf_image.setText(filePath);
				
				break;
			case "OK":
				Dialog.setVisible(false);
				String origin_select = selectedItem;
				
				// 1. 라디오 버튼에서 Movie가 선택되어서, Dialog에 panelMovie가 나타난 경우
				if(panelMovie.isVisible()==true) { 
					ismovieItem=true;
					String title = m_title.getText();
					m_title.setText("");
					String director = m_director.getText();
					m_director.setText("");
					String actor = m_actor.getText();
					m_actor.setText("");
					String genre = ""+m_genre.getSelectedItem();
					m_genre.setSelectedIndex(0);
					String grade = ""+m_grade.getSelectedItem();
					m_grade.setSelectedIndex(0);
					String year = ""+m_year.getSelectedItem();
					m_year.setSelectedIndex(0);
					ImageIcon icon = new ImageIcon(tf_poster.getText());
					tf_poster.setText("");
					int rate = m_sliderRate.getValue();
					m_sliderRate.setValue(5);
					String plot = m_plot.getText();
					m_plot.setText("");
					String comment = m_comment.getText();
					m_comment.setText("");
				

					// [수정]버튼이 눌려서, 다이얼로그가 띄워진 경우에는
					if(isMod==true) {   // 해당 영화 객체의 내용을 수정한다.
						int index1 = entire_v.indexOf(modMovie.title); 
						int index2 = movie_v.indexOf(modMovie.title);
						
						modMovie.setActor(actor);
						modMovie.setComment(comment);
						modMovie.setDirector(director);
						modMovie.setGenre(genre);
						modMovie.setGrade(grade);
						modMovie.setIcon(icon);
						modMovie.setPlot(plot);
						modMovie.setRate(rate);
						modMovie.setTitle(title);
						modMovie.setYear(year);
						// 아이템 컬렉션에도 변경된 내용을 저장한다.
						entire_v.set(index1, modMovie.title);
						movie_v.set(index2, modMovie.title);
						ItemDic.put(modMovie.title,modMovie);
						
					}
					//[추가]버튼이 눌려서, 다이얼로그가 띄워진 경우에는	
					else {  // 영화 객체를 생성해서 전체 컬렉션에 추가한다.
					Movie movie = new Movie(title, year, icon, rate, plot, comment, director, actor, genre, grade);
					ItemDic.put(title,movie);
					entire_v.add(movie.title);
					movie_v.add(movie.title);
					}
					
					// 변경사항이 리스트에도 반영되도록 한다.
					entire_list.setListData(entire_v);
					movie_list.setListData(movie_v);
					
					
					}
				
				// 2. 라디오 버튼에서 Book이 선택되어서, Dialog에 panelBook이 나타난 경우
				if(panelBook.isVisible()==true) {
					isbookItem=true;
					String title = b_title.getText();
					b_title.setText("");
					String author = b_author.getText();
					b_author.setText("");
					String publisher = b_publisher.getText();
					b_publisher.setText("");
					String year = ""+b_year.getSelectedItem();
					b_year.setSelectedIndex(0);
					ImageIcon icon = new ImageIcon(tf_image.getText());
					tf_image.setText("");
					int rate = b_sliderRate.getValue();
					b_sliderRate.setValue(5);
					String plot = b_plot.getText();
					b_plot.setText("");
					String comment = b_comment.getText();
					b_comment.setText("");
					
					// [수정]버튼이 눌려서, 다이얼로그가 띄워진 경우에는
					if(isMod==true) {	// 해당 도서 객체의 내용을 수정한다.
						int index1 = entire_v.indexOf(modBook.title);
						int index2 = book_v.indexOf(modBook.title);
						modBook.setAuthor(author);
						modBook.setComment(comment);
						modBook.setIcon(icon);
						modBook.setPlot(plot);
						modBook.setPublisher(publisher);
						modBook.setRate(rate);
						modBook.setTitle(title);
						modBook.setYear(year);
						// 아이템 컬렉션에도 변경된 내용을 저장한다.
						entire_v.set(index1, modBook.title);   
						book_v.set(index2, modBook.title);
						ItemDic.put(modBook.title,modBook);
						
					}
					//[추가]버튼이 눌려서, 다이얼로그가 띄워진 경우에는	
					else {	// 도서 객체를 생성해서 전체 컬렉션에 추가한다.
					Book book = new Book(title,year,icon,rate,plot,comment,author,publisher);
					ItemDic.put(title, book);
					entire_v.add(book.title);
					book_v.add(book.title);
					}
					// 변경사항이 리스트에도 반영되도록 한다.
					entire_list.setListData(entire_v);
					book_list.setListData(book_v);

				}
				// 다이얼로그가 띄어지기 이전에,<상세보기>에서 보여줬던 내용을, 다이얼로그 창에서의 작업을 마친이후에도 보여준다.
				selectedItem = origin_select;
				
				if(selectedItem==null)
					return;
				else {
					Item item = ItemDic.get(selectedItem);
					if(item instanceof Movie) {
						ismovieItem=true; isbookItem=false;
					}
					if(item instanceof Book) {
						ismovieItem=false; isbookItem=true;
					}
					selectedList.setSelectedValue(selectedItem, isShowing());
				}
				image.repaint();
				isMod=false;
				break;	
				
				}
		}
	}	
	// 이벤트 리스너: 라디오버튼이 선택된 경우의 이벤트 처리
	class MyItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.DESELECTED)
				return;
			// 라디오 버튼을 선택하면 해당 입력 양식이 보여지도록 한다.
			
			// 1. panelMovie에 포함된 라디오 버튼
			if(e.getSource()==rbMovie) {
				panelMovie.setVisible(true);
				panelBook.setVisible(false);
			}
			if(e.getSource()==rbBook) {
				panelMovie.setVisible(false);
				panelBook.setVisible(true);
				rbBook2.setSelected(true);
			}
			
			// 2. panelBook에 포함된 라디오 버튼
			if(e.getSource()==rbMovie2) {
				panelMovie.setVisible(true);
				panelBook.setVisible(false);
				rbMovie.setSelected(true);
			}
			if(e.getSource()==rbBook2) {
				panelMovie.setVisible(false);
				panelBook.setVisible(true);
			}
		
		}
	}
	public static void main(String[] args) {
		new myNotes();

		}
	}
