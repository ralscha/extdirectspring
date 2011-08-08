package ch.ralscha.starter.web;

import java.io.OutputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.starter.entity.User;
import ch.ralscha.starter.repository.UserCustomRepository;

@Controller
public class UserExport {

	@Autowired
	private UserCustomRepository userCustomRepository;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	@RequestMapping(value = "/usersExport.xls", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void userExport(HttpServletRequest request, HttpServletResponse response, Locale locale,
			@RequestParam(required = false) String filter) throws Exception {

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.addHeader("Content-disposition", "attachment;filename=users.xlsx");

		Workbook workbook = new XSSFWorkbook();

		CreationHelper createHelper = workbook.getCreationHelper();

		Font font = workbook.createFont();
		Font titleFont = workbook.createFont();

		font.setColor(IndexedColors.BLACK.getIndex());
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

		titleFont.setColor(IndexedColors.BLACK.getIndex());
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 10);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle normalStyle = workbook.createCellStyle();
		normalStyle.setFont(font);

		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setFont(titleFont);

		Sheet sheet = workbook.createSheet(messageSource.getMessage("user_users", null, locale));

		Row row = sheet.createRow(0);
		createCell(row, 0, "ID", titleStyle, createHelper);
		createCell(row, 1, messageSource.getMessage("user_username", null, locale), titleStyle, createHelper);
		createCell(row, 2, messageSource.getMessage("user_firstname", null, locale), titleStyle, createHelper);
		createCell(row, 3, messageSource.getMessage("user_lastname", null, locale), titleStyle, createHelper);
		createCell(row, 4, messageSource.getMessage("user_email", null, locale), titleStyle, createHelper);
		createCell(row, 5, messageSource.getMessage("user_enabled", null, locale), titleStyle, createHelper);

		Page<User> page = userCustomRepository.findWithFilter(filter, null);

		int rowNo = 1;
		for (User user : page) {
			row = sheet.createRow(rowNo);
			rowNo++;

			Cell cell = row.createCell(0);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(user.getId());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(user.getUserName());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(2);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(user.getFirstName());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(3);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(user.getName());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(4);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(user.getEmail());
			cell.setCellStyle(normalStyle);

			cell = row.createCell(5);
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			cell.setCellValue(user.isEnabled());
			cell.setCellStyle(normalStyle);
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);

		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}

	private void createCell(Row row, int column, String value, CellStyle style, CreationHelper createHelper) {
		Cell cell = row.createCell(column);
		cell.setCellValue(createHelper.createRichTextString(value));
		cell.setCellStyle(style);
	}

}
