package manager;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import models.*;
import java.util.ArrayList;

public class MemoryBookManager implements Serializable, BookManager {
    private static final long serialVersionUID = 1L;
    private static MemoryBookManager instance = null;
    private HashMap<Integer, Book> books;
    private int nextId = 1000; // 도서 ID 자동 증가
    private final String FILE_PATH = "books.post";

    private MemoryBookManager() {
        books = new HashMap<>();
    }

    public static MemoryBookManager getInstance() {
        if (instance == null) {
            synchronized (MemoryBookManager.class) {
                if (instance == null) {
                    instance = new MemoryBookManager();
                }
            }
        }
        return instance;
    }

    // 도서 추가 메서드
    public Book addBook(String title, String author) {
        Book book = new Book(nextId++, title, author);
        books.put(book.getId(), book);
        saveData(); // 도서 추가 시 저장
        return book;
    }

    // 도서 삭제 메서드
    public void removeBook(int id) {
        books.remove(id);
        saveData(); // 도서 삭제 시 저장
    }

    // 도서 ID로 검색
    public Book getBookById(int id) {
        return books.get(id);
    }

    // 제목 또는 저자로 도서 검색
    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(lowerKeyword) ||
                    book.getAuthor().toLowerCase().contains(lowerKeyword)) {
                results.add(book);
            }
        }
        return results;
    }

    // 모든 도서 로드
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // 파일이 없으면 초기화된 상태 유지
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            books = (HashMap<Integer, Book>) ois.readObject();
            // 다음 ID 설정
            for (int id : books.keySet()) {
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("도서 데이터를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 모든 도서 저장
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(books);
        } catch (IOException e) {
            System.out.println("도서 데이터를 저장하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
