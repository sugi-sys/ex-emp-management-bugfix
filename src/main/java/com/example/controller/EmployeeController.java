package com.example.controller;

import java.util.List;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;

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
	 * @param form 検索欄用のフォーム
	 * @param page 表示ページ（リクエストされなければ1ページ目を表示する）
	 * @return 従業員一覧画面
	 */
	@GetMapping("/showList")
    public String showList(Model model, SearchEmployeeForm form, @RequestParam(required = false) String page) {
		// ページをリクエストされなければ、1ページ目を表示
		if (page == null) {
			page = "1";
		}
		// ページ数に基づいてoffset値を変更
        Integer offset = offsetGenerator(page);

		// DBよりデータを全件取得
        List<Employee> employeeAllList = employeeService.showList();
		
		// ページング
		List<Employee> employeeList = paging(employeeAllList, offset);

		// 取得したデータより、名前のみを抽出（Autocomplete用）
		String[] nameList = nameExtractor();

		// 最大ページ数を計算
		Integer[] pages = maxPageGenerator(employeeAllList);

		// リクエストスコープに格納
		model = modelAdder(model, employeeAllList, employeeList, nameList, page, pages);
        
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
	@GetMapping("/search")
	public String search(Model model, SearchEmployeeForm form, @RequestParam(required = false) String page) {
		if (page == null) {
			page = "1";
		}
		// 検索欄が空欄なら
		if ("".equals(form.getName())) {
			return "redirect:/employee/showList";
		}

		Integer offset = offsetGenerator(page);
		List<Employee> employeeAllList = employeeService.search(form.getName());

		// 検索にヒットしなかったら
		if (employeeAllList.size() == 0) {
			model.addAttribute("nullList", "１件もありませんでした");
			return showList(model, form, "1");
		}

		List<Employee> employeeList = paging(employeeAllList, offset);
		String[] nameList = nameExtractor();
		Integer[] pages = maxPageGenerator(employeeAllList);
		model = modelAdder(model, employeeAllList, employeeList, nameList, page, pages);
		model.addAttribute("searchName", form.getName());

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

	/**
	 * 取得したリストをページングします.
	 * 
	 * @return ページングされた従業員リスト
	 */
	private List<Employee> paging(List<Employee> employeeAllList, Integer offset) {
		List<Employee> employeeList = new ArrayList<>();
		for (int i = offset; i < offset + 10; i++) {
			try {
				employeeList.add(employeeAllList.get(i));
			} catch (IndexOutOfBoundsException e) {
				return employeeList;
			}
		}
		return employeeList;
	}

	/**
	 * 全従業員の名前を取得します.
	 * 
	 * @return 全従業員の名前が入った配列
	 */
	private String[] nameExtractor() {
		List<Employee> employeeListForName = employeeService.collectName();
		String[] nameList = new String[employeeListForName.size()];
		for (int i = 0; i < nameList.length; i++) {
			nameList[i] = employeeListForName.get(i).getName();
		}
		return nameList;
	}

	/**
	 * リクエストされたページ数から適切なOFFSET値を返します.
	 * 
	 * @param page リクエストされたページ数
	 * @return OFFSET値
	 */
	private Integer offsetGenerator(String page) {
		return (Integer.parseInt(page) * 10) - 10;
	}

	/**
	 * 取得したリストから最大のページ数を返します.
	 * 
	 * @param employeeAllList 取得した従業員全件リスト
	 * @return 最大ページ数
	 */
	private Integer[] maxPageGenerator(List<Employee> employeeAllList) {
		int max = (int)Math.ceil(employeeAllList.size() / 10.0);
		Integer[] pages = new Integer[max];
		for (int i = 0; i < max; i++) {
			pages[i] = i + 1;
		}
		return pages;
	}

	/**
	 * データ群をリクエストスコープに格納します.
	 * 
	 * @param model リクエストスコープ引継用
	 * @param employeeList 取得した従業員リスト
	 * @param nameList 全従業員の名前が入った配列
	 * @param pageInfo Mapに格納されたページ情報
	 */
	private Model modelAdder(Model model, List<Employee> employeeAllList, List<Employee> employeeList, String[] nameList, String page, Integer[] pages) {
		model.addAttribute("employeeList", employeeList);
		model.addAttribute("listSize", employeeAllList.size());
        model.addAttribute("nameList", nameList);
		model.addAttribute("page", Integer.parseInt(page));
		model.addAttribute("pages", pages);
		return model;
	}
	

}
