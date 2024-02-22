package com.example.controller;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Employee;
import com.example.form.InsertEmployeeForm;
import com.example.form.SearchEmployeeForm;
import com.example.form.UpdateEmployeeForm;
import com.example.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@GetMapping("/showList")
	public String showList(Model model, SearchEmployeeForm form) {
		List<Employee> employeeList = employeeService.showList();
		String[] nameList = new String[employeeList.size()];

		// 取得したデータより、名前のみを抽出（Autocomplete用）
		for (int i = 0; i < nameList.length; i++) {
			nameList[i] = employeeList.get(i).getName();
		}
		model.addAttribute("employeeList", employeeList);
		model.addAttribute("nameList", nameList);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員を従業員名で曖昧検索する
	/////////////////////////////////////////////////////
	/**
	 * 曖昧検索した従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@PostMapping("/search")
	public String search(Model model, SearchEmployeeForm form) {
		if ("".equals(form.getName())) {
			return showList(model, form);
		}
		List<Employee> employeeList = employeeService.search(form.getName());
		if (employeeList.size() == 0) {
			model.addAttribute("nullList", "１件もありませんでした");
			return showList(model, form);
		}
		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@GetMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員登録画面を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員登録画面を出力します.
	 * 
	 * @param form 従業員新規登録用フォーム
	 * @return 従業員登録画面
	 */
	@GetMapping("/toInsert")
	public String toInsert(InsertEmployeeForm form, Model model) {
		return "employee/insert";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員を新規登録する
	/////////////////////////////////////////////////////
	/**
	 * 従業員を新規登録します.
	 * 
	 * @param form 従業員新規登録用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@PostMapping("/insert")
	public String insert(
		Model model,
		@Validated InsertEmployeeForm form,
		BindingResult bindingResult
		) {
		try {
			if (bindingResult.hasErrors()) {
				if (form.getImage().isEmpty()) {
					model.addAttribute("nullImage", "画像が添付されていません");
				}
				return toInsert(form, model);
			}
			if (form.getImage().isEmpty()) {
				model.addAttribute("nullImage", "画像が添付されていません");
				return toInsert(form, model);
			}
			// 採番されている従業員IDの最大値＋１を取得
			Integer id = employeeService.getMaxId() + 1;
			
			// 画像名の重複を防ぐため、従業員IDと同一になるようにリネーム
			String OriginalfileName = form.getImage().getOriginalFilename();
			String extension = OriginalfileName.replaceAll("^.*\\.(.*)$", "$1");
			String imageName = id + "." + extension;

			// 画像の保存処理
			byte[] bytes = form.getImage().getBytes();
			Path path = Paths.get("src/main/resources/static/img/" + imageName);
			Files.write(path, bytes);

			Employee employee = new Employee();
			BeanUtils.copyProperties(form, employee);
			employee.setId(id);
			employee.setImage(imageName);
			employee.setSalary(form.getIntSalary());
			employee.setDependentsCount(form.getIntDependentsCount());
			
			employeeService.insert(employee);
			return "redirect:/employee/showList";
		} catch (IOException e) {
			return "error/5xx";
		}
		
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@PostMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}
}
