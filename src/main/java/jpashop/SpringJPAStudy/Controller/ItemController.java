package jpashop.SpringJPAStudy.Controller;

import jpashop.SpringJPAStudy.domain.item.Book;
import jpashop.SpringJPAStudy.domain.item.Item;
import jpashop.SpringJPAStudy.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("items/new")
    public String createForm(BookForm bookForm, BindingResult result) {
        Book newBook = new Book();
        newBook.setName(bookForm.getName());
        newBook.setPrice(bookForm.getPrice());
        newBook.setStockQuantity(bookForm.getStockQuantity());
        newBook.setAuthor(bookForm.getAuthor());
        newBook.setIsbn(bookForm.getIsbn());

        itemService.saveItem(newBook);
        return "redirect:/items";
    }

    @GetMapping("items")
    public String list(Model model) {
        List<Item> allItems = itemService.findAll();
        model.addAttribute("items", allItems);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book itemBook = (Book) itemService.findItem(itemId);

        BookForm bookForm = new BookForm();
        bookForm.setId(itemBook.getId());
        bookForm.setName(itemBook.getName());
        bookForm.setPrice(itemBook.getPrice());
        bookForm.setStockQuantity(itemBook.getStockQuantity());
        bookForm.setAuthor(itemBook.getAuthor());
        bookForm.setIsbn(itemBook.getIsbn());

        model.addAttribute("form", bookForm);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItemForm(BookForm bookForm, BindingResult result) {
        // merge 사용
//        Book updateBook = new Book();
//        updateBook.setId(bookForm.getId());
//        updateBook.setName(bookForm.getName());
//        updateBook.setPrice(bookForm.getPrice());
//        updateBook.setStockQuantity(bookForm.getStockQuantity());
//        updateBook.setAuthor(bookForm.getAuthor());
//        updateBook.setIsbn(bookForm.getIsbn());
//        itemService.saveItem(updateBook); // 해당 함수 안에서 merge 호출

        // 영속성 컨텍스트 수정 (변경 감지 사용) - author나 isbn은 일단 생략
        itemService.update( bookForm.getId(),
                            bookForm.getName(),
                            bookForm.getPrice(),
                            bookForm.getStockQuantity());

        return "redirect:/items";
    }
}
