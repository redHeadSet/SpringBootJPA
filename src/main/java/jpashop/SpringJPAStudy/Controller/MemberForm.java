package jpashop.SpringJPAStudy.Controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.")    // 비어있다면, BindingResult 에서 확인 가능
    // https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#field-errors
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
