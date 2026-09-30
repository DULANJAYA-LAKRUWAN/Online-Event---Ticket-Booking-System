package com.eventcart.controller.admin;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Category;
import com.eventcart.entity.CategoryStatus;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.CategoryService;
import com.eventcart.service.impl.CategoryServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing Event Categories in the Admin portal.
 * Supports CRUD actions and AJAX asynchronous status toggling.
 */
@WebServlet(name = "AdminCategoryServlet", urlPatterns = {"/admin/categories"})
public class AdminCategoryServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final CategoryService categoryService;

    public AdminCategoryServlet() {
        this(new CategoryServiceImpl());
    }

    public AdminCategoryServlet(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        req.setAttribute("categories", categories);
        req.setAttribute("pageActive", "admin-categories");
        forward(req, resp, "admin/categories");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = getStringParameter(req, "action", "create");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        try {
            switch (action) {
                case "create":
                    handleCreate(req, resp);
                    break;
                case "edit":
                    handleEdit(req, resp);
                    break;
                case "delete":
                    handleDelete(req, resp);
                    break;
                case "toggle":
                    handleToggle(req, resp, isAjax);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown category action: " + action);
            }
        } catch (ValidationException ve) {
            logger.warn("Validation error in AdminCategoryServlet: {}", ve.getMessage());
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            } else {
                setFlash(req, "error", ve.getMessage());
                redirect(req, resp, "/admin/categories");
            }
        } catch (Exception ex) {
            logger.error("Error processing category action '{}': {}", action, ex.getMessage(), ex);
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + ex.getMessage());
            } else {
                setFlash(req, "error", "An unexpected error occurred: " + ex.getMessage());
                redirect(req, resp, "/admin/categories");
            }
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = getStringParameter(req, "name", "");
        String description = getStringParameter(req, "description", "");
        String iconClass = getStringParameter(req, "iconClass", "bi-tag");
        String statusStr = getStringParameter(req, "status", "ACTIVE");

        CategoryStatus status = "INACTIVE".equalsIgnoreCase(statusStr) ? CategoryStatus.INACTIVE : CategoryStatus.ACTIVE;
        categoryService.createCategory(name, description, iconClass, status);

        setFlash(req, "success", "Category '" + name + "' created successfully.");
        redirect(req, resp, "/admin/categories");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        String name = getStringParameter(req, "name", "");
        String description = getStringParameter(req, "description", "");
        String iconClass = getStringParameter(req, "iconClass", "bi-tag");
        String statusStr = getStringParameter(req, "status", "ACTIVE");

        CategoryStatus status = "INACTIVE".equalsIgnoreCase(statusStr) ? CategoryStatus.INACTIVE : CategoryStatus.ACTIVE;
        categoryService.updateCategory(id, name, description, iconClass, status);

        setFlash(req, "success", "Category '" + name + "' updated successfully.");
        redirect(req, resp, "/admin/categories");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        categoryService.deleteCategory(id);

        setFlash(req, "success", "Category processed successfully.");
        redirect(req, resp, "/admin/categories");
    }

    private void handleToggle(HttpServletRequest req, HttpServletResponse resp, boolean isAjax) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Category updated = categoryService.toggleStatus(id);

        if (isAjax) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", updated.getId());
            data.put("status", updated.getStatus().name());
            data.put("statusDisplay", updated.getStatus().getDisplayName());
            sendJsonOk(resp, "Category status updated to " + updated.getStatus().getDisplayName(), data);
        } else {
            setFlash(req, "success", "Category status updated to " + updated.getStatus().getDisplayName());
            redirect(req, resp, "/admin/categories");
        }
    }
}
