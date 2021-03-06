package sk.bazos.service;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.bazos.model.Category;
import sk.bazos.model.Photo;
import sk.bazos.repository.CategoryRepository;
import sk.bazos.service.util.CategoryUtil;
import sk.bazos.to.CategoryCreateDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("category")
@Api(value = "category", description = "Basic crud over category entity.")
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping(consumes = "multipart/*")
    public Long addCategory(@ModelAttribute CategoryCreateDto categoryCreateDto) {
        Category category = CategoryUtil.fromCreate(categoryCreateDto);
        return categoryRepository.save(category).getId();
    }


    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findCategoriesByParentIsNull();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable("id") Long id) {
        Optional<Category> byId = categoryRepository.findById(id);
        return byId.orElse(null);
    }



    @PostMapping("/{id}/subcategory")
    public Long addSubcategory(@PathVariable("id") Long id, @RequestBody(required = true) CategoryCreateDto categoryCreateDto) {
        Optional<Category> parentCategory = categoryRepository.findById(id);
        if (parentCategory.isPresent()) {
            Category category = CategoryUtil.fromCreate(categoryCreateDto);
            parentCategory.get().addSubcategory(category);
            return categoryRepository.save(category).getId();
        }
        return null;
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable(value = "id") Long id, @RequestBody Category categoryDetails) {

        Category categoryToupdate = categoryRepository.getOne(id);

        categoryToupdate.setTitle(categoryDetails.getTitle());
        if (categoryDetails.getPhoto() != null && categoryDetails.getPhoto().getId() != null) {
            final Photo photo = entityManager.getReference(Photo.class, categoryDetails.getPhoto().getId());
            categoryToupdate.setPhoto(photo);
        }
        return categoryRepository.save(categoryToupdate);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable(value = "id") Long id) {
        Category category = categoryRepository.getOne(id);
        categoryRepository.delete(category);


    }

}
