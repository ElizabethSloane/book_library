package org.example.web.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.log4j.Logger;
import org.example.app.exceptions.NotFoundException;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.example.web.dto.BookRegexToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Controller
@RequestMapping(value = "/books")
@Scope("singleton")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("regexToRemove", new BookRegexToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("regexToRemove", new BookRegexToRemove());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        }
        else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        }
        else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/removeByRegex")
    public String removeBookByRegex(@Valid BookRegexToRemove regexToRemove, BindingResult bindingResult, Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            throw new NotFoundException("Book is not found");
        }
        for (Book book: bookService.getAllBooks()) {
            if (book.getAuthor().equals(regexToRemove.getRegex()) ||
                    book.getTitle().equals(regexToRemove.getRegex()) ||
                    book.getSize().toString().equals(regexToRemove.getRegex())) {
                bookService.removeBookById(book.getId());
                break;
            }
        }


//        List<Book> books = bookService.getAllBooks();
//        for (Book book: books) {
//            if (book.getAuthor().equals(regexToRemove) || book.getTitle().equals(regexToRemove) || book.getSize().toString().equals(regexToRemove)) {
//                bookService.removeBookById(book.getId());
//                return "redirect:/books/shelf";
//            }
//        }

        return "redirect:/books/shelf";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") @NotEmpty MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new NotFoundException("No upload file");
        }
        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        //create dir
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "external_uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //create file
        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();

        logger.info("new file saved at: " + serverFile.getAbsolutePath());

        return "redirect:/books/shelf";
    }



    @ExceptionHandler(NotFoundException.class)
    public String handlerError(Model model, NotFoundException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/Not_found";
    }
}
