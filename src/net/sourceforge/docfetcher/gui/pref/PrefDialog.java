/*******************************************************************************
 * Copyright (c) 2011 Tran Nam Quang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tran Nam Quang - initial API and implementation
 *******************************************************************************/

package net.sourceforge.docfetcher.gui.pref;

import net.sourceforge.docfetcher.enums.Img;
import net.sourceforge.docfetcher.enums.SettingsConf;
import net.sourceforge.docfetcher.gui.UtilGui;
import net.sourceforge.docfetcher.util.Util;
import net.sourceforge.docfetcher.util.annotations.NotNull;
import net.sourceforge.docfetcher.util.annotations.VisibleForPackageGroup;
import net.sourceforge.docfetcher.util.gui.ConfigComposite;
import net.sourceforge.docfetcher.util.gui.FormDataFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Tran Nam Quang
 */
@VisibleForPackageGroup
public final class PrefDialog {
	
	private final Shell shell;
	@NotNull private Button okBt;
	
	public PrefDialog(@NotNull Shell parent) {
		Util.checkNotNull(parent);
		shell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.SHELL_TRIM);
		shell.setLayout(Util.createFillLayout(10));
		shell.setText("Preferences");
		shell.setImage(Img.PREFERENCES.get());
		SettingsConf.ShellBounds.PreferencesDialog.bind(shell);
		
		new ConfigComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL) {
			protected Control createContents(Composite parent) {
				return PrefDialog.this.createContents(parent);
			}
			protected Control createButtonArea(Composite parent) {
				return PrefDialog.this.createButtonArea(parent);
			}
		};
	}
	
	@NotNull
	private Control createContents(@NotNull Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(Util.createGridLayout(2, false, 0, 5));
		
		PrefOption[] checkOptions = new PrefOption[] {
			new CheckOption(
				"Show manual on startup",
				SettingsConf.Bool.ShowManualOnStartup),

			new CheckOption(
				"Use OR operator as default in queries (instead of AND)",
				SettingsConf.Bool.UseOrOperator),

			new CheckOption(
				"Hide program in System Tray after opening files",
				SettingsConf.Bool.HideOnOpen),

			new CheckOption(
				"Clear search history on exit",
				SettingsConf.Bool.ClearSearchHistoryOnExit),

			// TODO post-release-1.1: Implement this; requires saving and restoring the tree expansion state
//			new CheckOption(
//				"Reset location filter on exit",
//				SettingsConf.Bool.ResetLocationFilterOnExit),
		};
		
		PrefOption[] fieldOptions = new PrefOption[] {
			new ColorOption(
				"Highlight color:",
				SettingsConf.IntArray.PreviewHighlighting),
				
			new FontOption(
				"Preview font (normal):",
				UtilGui.getPreviewFontNormal()),
			
			new FontOption(
				"Preview font (fixed width):",
				UtilGui.getPreviewFontMono()),
			
			new HotKeyOption("Global hotkey:")
		};
		
		for (PrefOption checkOption : checkOptions)
			checkOption.createControls(comp);
		
		Label spacing = new Label(comp, SWT.NONE);
		GridData spacingGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		spacingGridData.heightHint = 3;
		spacing.setLayoutData(spacingGridData);
		
		for (PrefOption fieldOption : fieldOptions)
			fieldOption.createControls(comp);
		
		return comp;
	}
	
	@NotNull
	private Control createButtonArea(@NotNull Composite parent) {
		// TODO i18n
		Composite comp = new Composite(parent, SWT.NONE);
		
		Button helpBt = Util.createPushButton(comp, "help", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO now: implement
//				UtilFile.launch(Const.HELP_FILE_INDEXING);
			}
		});
		
		Button resetBt = Util.createPushButton(comp, "restore_defaults", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO now: implement
			}
		});
		
		okBt = Util.createPushButton(comp, "&OK", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO now: implement
			}
		});
		
		Button cancelBt = Util.createPushButton(comp, "&Cancel", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO now: implement
			}
		});
		
		comp.setLayout(new FormLayout());
		FormDataFactory fdf = FormDataFactory.getInstance();
		fdf.margin(0).top().bottom().minWidth(75).applyTo(helpBt);
		fdf.left(helpBt, 5).applyTo(resetBt);
		fdf.unleft().right().applyTo(cancelBt);
		fdf.right(cancelBt, -5).applyTo(okBt);
		
		return comp;
	}

	public void open() {
		okBt.setFocus();
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}
	}
	
	@NotNull
	static StyledLabel createLabeledStyledLabel(@NotNull Composite parent,
														@NotNull String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		StyledLabel text = new StyledLabel(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return text;
	}

	private static final class CheckOption extends PrefOption {
		private final SettingsConf.Bool enumOption;
		
		public CheckOption(	@NotNull String labelText,
							@NotNull SettingsConf.Bool enumOption) {
			super(labelText);
			this.enumOption = enumOption;
		}
		public void createControls(@NotNull Composite parent) {
			Button bt = Util.createCheckButton(parent, labelText);
			bt.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
			bt.setSelection(enumOption.get());
		}
	}
	
	private static final class HotKeyOption extends PrefOption {
		public HotKeyOption(@NotNull String labelText) {
			super(labelText);
		}
		public void createControls(@NotNull Composite parent) {
			final StyledLabel st = createLabeledStyledLabel(parent, labelText);
			st.setCursor(st.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			
			int[] hotkey = SettingsConf.IntArray.HotkeyToFront.get();
			st.setText(HotkeyDialog.toString(hotkey));
			
			st.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					HotkeyDialog dialog = new HotkeyDialog(st.getShell());
					int[] hotkey = dialog.open();
					st.setText(HotkeyDialog.toString(hotkey));
				}
			});
		}
	}

	static abstract class PrefOption {
		protected final String labelText;

		public PrefOption(@NotNull String labelText) {
			this.labelText = labelText;
		}
		// Subclassers must set grid datas on the created controls, assuming
		// a two-column grid layout
		protected abstract void createControls(@NotNull Composite parent);
	}

}