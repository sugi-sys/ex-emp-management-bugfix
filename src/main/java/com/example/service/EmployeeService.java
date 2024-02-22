package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Employee;
import com.example.repository.EmployeeRepository;

/**
 * 従業員情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * 従業員情報を全件取得します.
	 * 
	 * @return 従業員情報一覧
	 */
	public List<Employee> showList() {
		List<Employee> employeeList = employeeRepository.findAll();
		return employeeList;
	}

	/**
	 * 従業員情報を全件、名前のみ取得します.
	 * 
	 * @return 従業員情報一覧
	 */
	public List<Employee> collectName() {
		List<Employee> employeeList = employeeRepository.findAllName();
		return employeeList;
	}

	/**
	 * 従業員情報を名前で検索して取得します.
	 * 
	 * @param name 検索したい従業員名
	 * @return 検索結果の従業員一覧
	 */
	public List<Employee> search(String name) {
		List<Employee> employeeList = employeeRepository.findByName(name);
		return employeeList;
	}

	/**
	 * 従業員情報の内、採番されているIDの最大値を取得します.
	 * 
	 * @return 採番されているIDの最大値
	 */
	public Integer getMaxId() {
		Integer maxId = employeeRepository.getMaxId();
		return maxId;
	}

	/**
	 * 従業員情報を取得します.
	 * 
	 * @param id ID
	 * @return 従業員情報
	 * @throws org.springframework.dao.DataAccessException 検索されない場合は例外が発生します
	 */
	public Employee showDetail(Integer id) {
		Employee employee = employeeRepository.load(id);
		return employee;
	}

	/**
	 * 従業員情報を更新します.
	 * 
	 * @param employee 更新した従業員情報
	 */
	public void update(Employee employee) {
		employeeRepository.update(employee);
	}

	/**
	 * 従業員情報を新規登録します.
	 * synchronized修飾子をもちます
	 * 
	 * @param employee 新規登録した従業員情報
	 */
	public synchronized void insert(Employee employee) {
		employeeRepository.insert(employee);
	}
}
