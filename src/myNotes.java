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
	String search_topic="����";
	JTextArea tf_plot, tf_comment;
	MyPanel mp;
	String title, year, director,actor,genre,grade;
	String author, publisher;
	Movie modMovie; Book modBook; // �׸� ������ ���� �ʿ��� ����
	int rate;
	JPanel image,plot, comment;
	String selectedItem;
	JList entire_list, movie_list, book_list, search_list;
	JList selectedList;
	Vector entire_v,movie_v,book_v,search_v;
	Thread th;
	JLabel timerLabel;
	
	public myNotes() {
		// HashMap �÷��� ����
		ItemDic = new HashMap<String,Item>(); // ����� Item(Movie/Book)�� ����ϴ� HashMap
		
		setTitle("JAVA 04�й� 1912062 ������");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		c = getContentPane();
		c.setLayout(new BorderLayout());
		createMenu(); // �޴� ����
		createNote(); // Note����
		createInputDialog(); // ��ȭ���� ����
		setSize(805,770);
		setVisible(true);
		th.start(); // ������ ����
		
	}
	private void createMenu() {
		JMenuBar mb = new JMenuBar();
		JMenuItem[]menuItem1 = new JMenuItem[3];
		JMenuItem[]menuItem2 = new JMenuItem[1];
		
		String[] itemTitle1={"�ҷ�����","�����ϱ�","����"};
		String[] itemTitle2 = {"���� ����"};
		
		JMenu fileMenu = new JMenu("����");
		JMenu helpMenu = new JMenu("����");
		
		MenuActionListener listener = new MenuActionListener();
		// ���� �޴���, 3���� �޴��������� ���δ�.
		for(int i=0; i<menuItem1.length; i++) {
			menuItem1[i] = new JMenuItem(itemTitle1[i]);
			menuItem1[i].addActionListener(listener); // �޴������ۿ� Action������ ���
			fileMenu.add(menuItem1[i]);			
			if(i==1)
				fileMenu.addSeparator(); // �и��� ����
		} 
		
		// ���� �޴���, 1���� �޴��������� ���δ�.
		for(int i=0;i<menuItem2.length;i++) {
			menuItem2[i]=new JMenuItem(itemTitle2[i]);
			menuItem2[i].addActionListener(listener); // �޴������ۿ� Action������ ���
			helpMenu.add(menuItem2[i]);
		}
		
		mb.add(fileMenu);
		mb.add(helpMenu);
		setJMenuBar(mb);
	
	}
	class MenuActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			JFileChooser chooser = new JFileChooser(); // FileChooser��ü ����
			switch(cmd){
			case "����":
				int result = JOptionPane.showConfirmDialog(null, "�����Ͻðڽ��ϱ�?","���� Ȯ��",JOptionPane.YES_NO_OPTION);
				if(result== JOptionPane.YES_OPTION) 
					System.exit(0); // ���α׷� ����
				break;
			case "���� ����":
				JOptionPane.showMessageDialog(null, "MyNotes �ý��� v1.0�Դϴ�.", "Message", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "�ҷ�����":
				int ret1 = chooser.showOpenDialog(null); // ���� ���� ���̾�α� ���
				if(ret1==JFileChooser.APPROVE_OPTION) {  
					try {
						String pathName = chooser.getSelectedFile().getPath(); // ����ڰ� ������ ���� �̸� �˾Ƴ���
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathName)); 
						// ���� ������ �ҷ��ͼ� �÷��� ��ü�� �����ϵ��� �Ѵ�.
						ItemDic = (HashMap<String,Item>)ois.readObject(); 
						entire_v = (Vector<Item>)ois.readObject();
						movie_v = (Vector<Item>) ois.readObject();
						book_v = (Vector<Item>)ois.readObject();
						
						// �ҷ��� �׸���� �ٽ� ����Ʈ�� �����ش�.
						entire_list.setListData(entire_v); 
						movie_list.setListData(movie_v);
						book_list.setListData(book_v);
					
					}
					catch(IOException e1) {
						System.out.println("���� ��� ����");
					} 
					catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} 
				}
				break;
			case "�����ϱ�":
				int ret2 = chooser.showSaveDialog(null); // ���� ���� ���̾�α� ���
				if(ret2==JFileChooser.APPROVE_OPTION) {
					try {
					String pathName = chooser.getSelectedFile().getPath(); // ����ڰ� ������ ���� �̸� �˾Ƴ���
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathName)); 
					
					//���õ� ���Ͽ� �÷����� �����Ѵ�.
					oos.writeObject(ItemDic);
					oos.writeObject(entire_v); 
					oos.writeObject(movie_v);
					oos.writeObject(book_v);
					oos.close();
					}
					catch(IOException e2){
						System.out.println("���� �Է� ����");
					}
				}
				break;
			}
		}	
		}
	// Runnable�� Ŭ������ ����
	class TimerRunnable implements Runnable{ 
		private JLabel timerLabel;
		public TimerRunnable(JLabel timerLabel) {
			this.timerLabel=timerLabel;
		}
		@Override
		public void run() { // ������ �ڵ� �ۼ�
			int n=0;
			while(true) {
				Calendar cal = Calendar.getInstance();
				int year= cal.get(Calendar.YEAR);
				int month= cal.get(Calendar.MONTH)+1;
				int day= cal.get(Calendar.DAY_OF_MONTH);
				int hour= cal.get(Calendar.HOUR);
				int minute = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);
				timerLabel.setText(year + "�� " + month + "�� " +day+ "�� " +hour+ ":" +minute + ":" +second);
			
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
		
		timerLabel = new JLabel(); // ���� �����ϴ� ���� ����ð��� �����ִ� ��
		timerLabel.setFont(new Font("����",Font.PLAIN,15));
		TimerRunnable runnable = new TimerRunnable(timerLabel); 
		th = new Thread(runnable); // ������ ����
		panelTop.add(p_title);
		panelTop.add(timerLabel);
		
		// Center
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(null);
		
		// Center(1): ���� ����
		JPanel tab = new JPanel();
		tab.setSize(205, 600);
		tab.setLocation(0,10);
		tab.setLayout(new GridLayout());
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		
		
		MyListListener mll = new MyListListener();
		entire_v = new Vector<Item>();  // ItemCollectionsŬ���� ����: List�� Item�� �����ϴ� ����
		movie_v = new Vector<Item>();
		book_v = new Vector<Item>();
		search_v = new Vector<Item>();
		
		//����Ʈ ��ü ����
		entire_list = new JList<String>(entire_v);  // [��ü] �� ���ý� �����ִ� ����Ʈ
		entire_list.addListSelectionListener(mll);
	
		movie_list = new JList<String>(movie_v);    // [��ȭ] �� ���ý� �����ִ� ����Ʈ
		movie_list.addListSelectionListener(mll);
		
		book_list = new JList<String>(book_v);		// [����] �� ���ý� �����ִ� ����Ʈ
		book_list.addListSelectionListener(mll);
		
		JPanel sr_Panel = new JPanel();				// [�˻�] �� ���ý� �����ִ� Panel
		sr_Panel.setLayout(new BorderLayout(5,5));
		JPanel search_Panel = new JPanel();			// [�˻�]��(1)-���, �˻�â
		search_Panel.setLayout(new BorderLayout(5,5));
		String[] sr_type_info = {"����","����"};
		JComboBox<String>sr_type = new JComboBox<String>(sr_type_info);
		search_Panel.add(sr_type,BorderLayout.WEST);
		JTextField sr_input = new JTextField(10);
		search_Panel.add(sr_input,BorderLayout.CENTER);
		JButton sr_btn = new JButton("�˻�");
		sr_btn.setSize(5, 5);
		search_Panel.add(sr_btn,BorderLayout.EAST);
		sr_Panel.add(search_Panel,BorderLayout.NORTH);
		
		search_list = new JList<String>(search_v);	// [�˻�]��(2)-�ϴ�, �˻���� ����� ���� ����Ʈ
		search_list.addListSelectionListener(mll);
		sr_Panel.add(search_list,BorderLayout.CENTER);
		
					
		// �̺�Ʈ ������: �˻� Panel�� �޺��ڽ����� ���õ� ������("����/"����"), search_topic�� �����Ѵ�.
		sr_type.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String>cb = (JComboBox<String>)e.getSource();
				search_topic = ""+cb.getSelectedItem();
				
			}
			
		});
		// �̺�Ʈ ������: �˻� Panel���� "�˻�"��ư�� ������, �˻������� ����Ѵ�.
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
					if(search_topic.equals("����")) {		// [����]���� �˻��� ���
					int sr_index = title.indexOf(sr_key); // �˻�� ���Ե� ������� ��� �˻��ȴ�.
					
					if(sr_index >-1) {
						search_v.add(title);
					
					
						search_list.setListData(search_v);
						image.repaint();
						}
					}
					if(search_topic.equals("����")) {    // [����]���� �˻��� ���
						Item item = ItemDic.get(title);  
						int rate = item.rate;
						
						if(Integer.parseInt(sr_key)<=rate) {  // �˻��� �̻��� �׸���� ��� �˻��ȴ�.
							search_v.add(title);
							
						
							search_list.setListData(search_v);
							image.repaint();
						}
					}
			
				}
				if(search_v.isEmpty()==true) { // �˻� ����� ���� ���
					JOptionPane.showMessageDialog(null, "["+sr_key+"]"+" �˻� ����� �����ϴ�.", "Message",JOptionPane.INFORMATION_MESSAGE);
					
				}
		}});
		
		
		entire_list.setFixedCellWidth(55);
		movie_list.setFixedCellWidth(55);
		book_list.setFixedCellWidth(55);
		search_list.setFixedCellWidth(55);
		
		// ���� ����
		pane.addTab("��ü", entire_list);
		pane.addTab("��ȭ", movie_list);
		pane.addTab("����", book_list);
		pane.addTab("�˻�", sr_Panel);
		tab.add(pane);
		panelCenter.add(tab);
		
		// �̺�Ʈ ������: ���� �����ϸ�, �ش� ���� ����Ʈ�� ���õǾ� �ִ�, Item�� <�󼼺���>ȭ�鿡 ����.
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
		
		// Center(2): �� ����
		JPanel detail = new JPanel();
		detail.setLocation(210, 10); detail.setSize(570,600);
		detail.setLayout(null);
		Border detailtBorder = BorderFactory.createTitledBorder("�� ����"); // ������ �ٿ��� ��踦 ����
		detail.setBorder(detailtBorder);
		
		// �󼼺���(1) �̹��� 
		image = new JPanel();
		image.setLayout(new GridLayout(0,1));
		image.setLocation(10,20); image.setSize(540,250);
		mp = new MyPanel();
		image.add(mp);
		detail.add(image);
		
		// �󼼺���(2) �ٰŸ�
		plot = new JPanel();
		Border plotBorder = BorderFactory.createTitledBorder("�ٰŸ�"); // ������ �ٿ��� ��踦 ����
		plot.setBorder(plotBorder);
		tf_plot = new JTextArea(6,52);
		plot.add(new JScrollPane(tf_plot));
		detail.add(plot);
		plot.setLocation(10,270);plot.setSize(550,150);
		
		// �󼼺���(3) ������
		comment = new JPanel();
		Border commentBorder = BorderFactory.createTitledBorder("������"); // ������ �ٿ��� ��踦 ����
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
		// Bottom(1): �߰� ��ư
		btnAdd = new JButton("�߰�");
		btnAdd.addActionListener(bal);
		// Bottom(2): ���� ��ư
		btnMod = new JButton("����");
		btnMod.setSize(10,10); btnMod.setLocation(0, 750);
		btnMod.addActionListener(bal);
		// Bottom(3): ���� ��ư
		btnDel = new JButton("����");	
		btnDel.setSize(10,10);btnDel.setLocation(0, 500);
		btnDel.addActionListener(bal);
		
		panelBottom.add(btnAdd);
		for(int i=0;i<20;i++) {  //��ư ������ ������ ����� ���� ���Ե�, �ǹ̾��� JPanel
			panelBottom.add(new JPanel());
		}
		panelBottom.add(btnMod); panelBottom.add(btnDel);
		for(int i=0;i<10;i++) {  //��ư ������ ������ ����� ���� ���Ե�, �ǹ̾��� JPanel
			panelBottom.add(new JPanel());
		}
		
		c.add(panelTop,BorderLayout.NORTH);
		c.add(panelCenter, BorderLayout.CENTER);
		c.add(panelBottom, BorderLayout.SOUTH);
	}
	// Center(2)�󼼺�����  (1)'�̹���'�гο� ���Ե� �κ�
	class MyPanel extends JPanel{
		public void paintComponent(Graphics g) {  // ����Ʈ���� ���õ� �׸��� ������ ����, ���� �ٸ� '�׸���'�۾� ����
			super.paintComponent(g);
			
			if(ismovieItem==false && isbookItem == false) {
				g.drawLine(10, 20, 200, 220);
				g.drawLine(200, 20, 10, 220);
				g.drawString("�̹��� ����",85, 125);
			}
	
			if(ismovieItem==true) {  // Movie�� ���õ� ���
		
				Image img = selectedItem_icon.getImage();
				g.drawImage(img, 10, 10, 200, 230, this);
			
				g.drawString("����          "+title, 240, 30);
				g.drawString("����          "+director,240,60);
				g.drawString("���          "+actor, 240, 90);
				g.drawString("�帣          "+genre, 240, 120);
				g.drawString("���          "+grade, 240, 150);
				g.drawString("�����⵵     "+year, 240, 180);
				g.drawString("����           "+rate, 240, 210);
			ismovieItem = false;
		}
			if(isbookItem == true) {  // Book�� ���õ� ���
				Image img = selectedItem_icon.getImage();
				g.drawImage(img, 10, 10, 200, 230, this);
				
				g.drawString("����          "+title, 240, 30);
				g.drawString("����          "+author,240,70);
				g.drawString("���ǻ�        "+publisher, 240, 110);
				g.drawString("���ǳ⵵     "+year, 240, 150);
				g.drawString("����           "+rate, 240, 190);
				isbookItem = false;
			}
	}
		
	}
	// �̺�Ʈ ������: ����Ʈ���� �׸��� ���õǸ�, �ش�Item���� ��������
	class MyListListener implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedList = (JList)e.getSource();
			
			selectedItem = (String)selectedList.getSelectedValue();
			Item item = ItemDic.get(selectedItem);
			
			if(item instanceof Movie) {  // Movie�� ���õ� ���
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
			if(item instanceof Book) {   // Book�� ���õ� ���
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
	// �̺�Ʈ ������: [�߰�],[����],[����] ��ư�� ������ �� �̺�Ʈ ó��
	class btnActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btnAdd) {  // [�߰�] ��ư�� ���� ���
				Dialog.setVisible(true);  // �Է� ��ȭ���ڸ� ����.
				
				rbMovie.setSelected(true);
				rbBook2.setSelected(true);
				panelMovie.setVisible(true);
				panelBook.setVisible(false);
			}
			
			if(e.getSource()==btnMod) {   // [����] ��ư�� ���� ���
				
				Dialog.setVisible(true);  	// ������ ����, ��ȭ���ڰ� ������� �Ѵ�.
				Item item = ItemDic.get(selectedItem);
				
				if(item instanceof Movie) {  // Movie �׸��� �����Ǵ� ���
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
				if(item instanceof Book) {	// Book �׸��� �����Ǵ� ���
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
		
			if(e.getSource()==btnDel) {     // [����] ��ư�� ���� ���
				Item item = ItemDic.get(selectedItem);
				
				if(item instanceof Movie) {  // Movie�׸��� �����Ǵ� ���
					Movie delMovie = (Movie)item;
					// ���� Ȯ�� �޽����� ����, Yes��ư�� ������ �ش� �׸��� �÷��ǿ��� �����Ѵ�.
					int result = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?","���� Ȯ��",JOptionPane.YES_NO_OPTION);
						if(result== JOptionPane.YES_OPTION) {
							ItemDic.remove(delMovie);
							entire_v.remove(delMovie.title);
							movie_v.remove(delMovie.title);
							search_v.remove(delMovie.title);
					}
				}
				if(item instanceof Book) {		// Book�׸��� �����Ǵ� ���
					Book delBook = (Book)item;
					// ���� Ȯ�� �޽����� ����, Yes��ư�� ������ �ش� �׸��� �÷��ǿ��� �����Ѵ�.
					int result = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?","���� Ȯ��",JOptionPane.YES_NO_OPTION);
					if(result== JOptionPane.YES_OPTION) {
						ItemDic.remove(delBook);
						entire_v.remove(delBook.title);
						book_v.remove(delBook.title);
						search_v.remove(delBook.title);
					}
				}
				// ���� �� ���Ŀ�, ����� ����Ʈ ����� �����ش�.
				entire_list.setListData(entire_v);
				movie_list.setListData(movie_v);
				book_list.setListData(book_v);
				search_list.setListData(search_v);
				tf_plot.setText("");tf_comment.setText("");
				image.repaint();
			}
		}
	}
	// �׸� �߰�, ������ ���� ��ȭ����
	private void createInputDialog() {
		Dialog = new JDialog();
		Dialog.setSize(400,780);
		Dialog.setTitle("�Է�");
		MyActionListener mal = new MyActionListener();
		
		
		// �޺��ڽ��� �������� �����ϴ� �迭
		String[]genre_info = {"���","���/�θǽ�","����","ȣ��"};
		String[]grade_info = {"����","12�� �̻�","15�� �̻�","û�ҳ� �����Ұ�"};
		String[]year_info = {"2020","2019","2018","2017","2016","2015"};
		String[] movieInfo = {"����","����","���","�帣","���","�����⵵","������","����","�ٰŸ�","","������",""};
		String[] bookInfo = {"����","����","���ǻ�","���ǳ⵵","å�̹���","����","å�Ұ�","","������","","",""};
		
		//Dialog(1) panelMovie: ���� ��ư���� Movie�� ���õ� ��, Dialog�� ��Ÿ���� �г�
		panelMovie = new JPanel();
		Border movieBorder = BorderFactory.createTitledBorder("��ȭ ����"); // ������ �ٿ��� ��踦 ����
		panelMovie.setBorder(movieBorder);
		panelMovie.setSize(400,750);
		panelMovie.setLayout(new BorderLayout(5,5));
		
		// panelTop-(1) Top, ���� ��ư
		JPanel panelTop = new JPanel();
		ButtonGroup type = new ButtonGroup(); // ��ư �׷� ��ü ����
		rbMovie = new JRadioButton("Movie",true);// Movie�� ���õ� ����
		rbBook = new JRadioButton("Book");
		MyItemListener mil = new MyItemListener();
		rbMovie.addItemListener(mil); 
		rbBook.addItemListener(mil);
		type.add(rbMovie); type.add(rbBook); // ���� ��ư ��ư �׷쿡 ���
		panelTop.add(rbMovie);panelTop.add(rbBook);
		panelMovie.add(panelTop,BorderLayout.NORTH);
		
		// panelLabel-(2) West, �Է��ؾ��� ��ϵ��� ����("����","�帣...)
		JPanel panelLabel = new JPanel();
		panelLabel.setLayout(new GridLayout(0,1,5,0));
		for(int i=0;i<movieInfo.length;i++) {
			panelLabel.add(new JLabel(movieInfo[i]));
		}
		panelMovie.add(panelLabel,BorderLayout.WEST);
		
		// panelCenter-(3) Center, Movie�⺻���� + �ٰŸ�,������
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
		
		
		JPanel poster = new JPanel();    // poster �г�: ���ϰ�� + �̹��� ���� '�ҷ�����'��ư
		tf_poster = new JTextField(15);
		JButton m_btnUpload = new JButton("�ҷ�����");
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
		
		// panelMovieInfo2 - Movie�� �ٰŸ� �� ������ �Է�
		JPanel panelMovieInfo2 = new JPanel();
		panelMovieInfo2.setLayout(new GridLayout(0,1,5,5));
		m_plot = new JTextArea(5,25);
		m_comment = new JTextArea(5,25);
		panelMovieInfo2.add(new JScrollPane(m_plot));
		panelMovieInfo2.add(new JScrollPane(m_comment));
		panelCenter.add(panelMovieInfo2,BorderLayout.CENTER);
		panelMovie.add(panelCenter,BorderLayout.CENTER);
		
		// panelTop-(4) South, OK��ư
		JPanel SouthPanel = new JPanel();
		SouthPanel.setLayout(new FlowLayout());
		JButton m_ok = new JButton("OK");
		SouthPanel.add(m_ok);
		m_ok.addActionListener(mal);
		panelMovie.add(SouthPanel,BorderLayout.SOUTH);
	
		Dialog.add(panelMovie);
		
		
		
		// Dialog(2) panelBook: ���� ��ư���� Book�� ���õ� ��, Dialog�� ��Ÿ���� �г�
		panelBook = new JPanel();
		panelBook.setVisible(false);
		Border bookBorder = BorderFactory.createTitledBorder("���� ����"); // ������ �ٿ��� ��踦 ����
		panelBook.setBorder(bookBorder);
		panelBook.setSize(400,550);
		panelBook.setLayout(new BorderLayout(5,5));
		
		// b_panelTop-(1) Top, ���� ��ư
		JPanel b_panelTop = new JPanel();
		ButtonGroup type2 = new ButtonGroup(); // ��ư �׷� ��ü ����
		rbMovie2 = new JRadioButton("Movie");
		rbMovie2.addItemListener(mil); // ���� ��ư�� Item������ ���
		rbBook2 = new JRadioButton("Book",true);
		rbBook2.addItemListener(mil); // ���� ��ư�� Item������ ���
		type2.add(rbMovie2); type2.add(rbBook2); // ���� ��ư ��ư �׷쿡 ���
		b_panelTop.add(rbMovie2);b_panelTop.add(rbBook2);
		panelBook.add(b_panelTop,BorderLayout.NORTH);
		
		// b_panelLabel-(2) West, �Է��ؾ��� ��ϵ��� ����("����","����"...)
		JPanel b_panelLabel = new JPanel();
		b_panelLabel.setLayout(new GridLayout(0,1,5,0));
		for(int i=0;i<bookInfo.length;i++) {
			b_panelLabel.add(new JLabel(bookInfo[i]));
		}
		panelBook.add(b_panelLabel,BorderLayout.WEST);
		
		// panelCenter-(3) Center, Book�⺻���� + �ٰŸ�,������
		JPanel b_panelCenter = new JPanel();
		JPanel panelBookInfo = new JPanel();
		panelBookInfo.setLayout(new GridLayout(0,1,10,10));
		b_title = new JTextField();
		b_author = new JTextField();
		b_publisher = new JTextField();
		b_year = new JComboBox<String>(year_info);
		
		JPanel image = new JPanel();// image �г�: ���ϰ�� + �̹��� ���� '�ҷ�����'��ư
		tf_image = new JTextField(15);
		JButton b_btnUpload = new JButton("�ҷ�����");
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
		
		// panelMovieInfo2 - Book�� �ٰŸ� �� ������ �Է�
		JPanel panelBookInfo2 = new JPanel();
		panelBookInfo2.setLayout(new GridLayout(0,1,5,5));
		b_plot = new JTextArea(5,25);
		b_comment = new JTextArea(5,25);
		panelBookInfo2.add(new JScrollPane(b_plot));
		panelBookInfo2.add(new JScrollPane(b_comment));
		b_panelCenter.add(panelBookInfo2,BorderLayout.CENTER);
		panelBook.add(b_panelCenter,BorderLayout.CENTER);
		
		// panelTop-(4) South, OK��ư
		JPanel SouthPanel2 = new JPanel();
		SouthPanel2.setLayout(new FlowLayout());
		JButton b_ok = new JButton("OK");
		SouthPanel2.add(b_ok);
		panelBook.add(SouthPanel2,BorderLayout.SOUTH);
		b_ok.addActionListener(mal);
		
		Dialog.add(panelBook);
	}
	
	// �̺�Ʈ ������: ���̾�α� ���� [�ҷ�����],[OK]��ư�� �̺�Ʈ ó��
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			JFileChooser chooser = new JFileChooser();
			switch(cmd) {
			case "�ҷ�����":
				String filePath=null;
				int ret1 = chooser.showOpenDialog(null);  // ���� ���� â�� �������
				if(ret1 == JFileChooser.APPROVE_OPTION)   
					filePath = chooser.getSelectedFile().getPath();
				if(panelMovie.isVisible()==true)  // Dialog�� panelMovie�� ��Ÿ�� ���
					tf_poster.setText(filePath);
				if(panelBook.isVisible()==true)   // Dialog�� panelBook�� ��Ÿ�� ���
					tf_image.setText(filePath);
				
				break;
			case "OK":
				Dialog.setVisible(false);
				String origin_select = selectedItem;
				
				// 1. ���� ��ư���� Movie�� ���õǾ, Dialog�� panelMovie�� ��Ÿ�� ���
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
				

					// [����]��ư�� ������, ���̾�αװ� ����� ��쿡��
					if(isMod==true) {   // �ش� ��ȭ ��ü�� ������ �����Ѵ�.
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
						// ������ �÷��ǿ��� ����� ������ �����Ѵ�.
						entire_v.set(index1, modMovie.title);
						movie_v.set(index2, modMovie.title);
						ItemDic.put(modMovie.title,modMovie);
						
					}
					//[�߰�]��ư�� ������, ���̾�αװ� ����� ��쿡��	
					else {  // ��ȭ ��ü�� �����ؼ� ��ü �÷��ǿ� �߰��Ѵ�.
					Movie movie = new Movie(title, year, icon, rate, plot, comment, director, actor, genre, grade);
					ItemDic.put(title,movie);
					entire_v.add(movie.title);
					movie_v.add(movie.title);
					}
					
					// ��������� ����Ʈ���� �ݿ��ǵ��� �Ѵ�.
					entire_list.setListData(entire_v);
					movie_list.setListData(movie_v);
					
					
					}
				
				// 2. ���� ��ư���� Book�� ���õǾ, Dialog�� panelBook�� ��Ÿ�� ���
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
					
					// [����]��ư�� ������, ���̾�αװ� ����� ��쿡��
					if(isMod==true) {	// �ش� ���� ��ü�� ������ �����Ѵ�.
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
						// ������ �÷��ǿ��� ����� ������ �����Ѵ�.
						entire_v.set(index1, modBook.title);   
						book_v.set(index2, modBook.title);
						ItemDic.put(modBook.title,modBook);
						
					}
					//[�߰�]��ư�� ������, ���̾�αװ� ����� ��쿡��	
					else {	// ���� ��ü�� �����ؼ� ��ü �÷��ǿ� �߰��Ѵ�.
					Book book = new Book(title,year,icon,rate,plot,comment,author,publisher);
					ItemDic.put(title, book);
					entire_v.add(book.title);
					book_v.add(book.title);
					}
					// ��������� ����Ʈ���� �ݿ��ǵ��� �Ѵ�.
					entire_list.setListData(entire_v);
					book_list.setListData(book_v);

				}
				// ���̾�αװ� ������� ������,<�󼼺���>���� ������� ������, ���̾�α� â������ �۾��� ��ģ���Ŀ��� �����ش�.
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
	// �̺�Ʈ ������: ������ư�� ���õ� ����� �̺�Ʈ ó��
	class MyItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.DESELECTED)
				return;
			// ���� ��ư�� �����ϸ� �ش� �Է� ����� ���������� �Ѵ�.
			
			// 1. panelMovie�� ���Ե� ���� ��ư
			if(e.getSource()==rbMovie) {
				panelMovie.setVisible(true);
				panelBook.setVisible(false);
			}
			if(e.getSource()==rbBook) {
				panelMovie.setVisible(false);
				panelBook.setVisible(true);
				rbBook2.setSelected(true);
			}
			
			// 2. panelBook�� ���Ե� ���� ��ư
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
