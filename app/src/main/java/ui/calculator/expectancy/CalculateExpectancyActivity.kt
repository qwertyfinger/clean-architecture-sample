/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.calculator.expectancy

import android.animation.ValueAnimator
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.TooltipCompat
import android.text.InputFilter.LengthFilter
import android.text.SpannableString
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.qwertyfinger.cleanarchitecturesample.R
import data.settings.model.CalculationUnits
import data.settings.model.CalculationUnits.KILOMETER
import data.settings.model.CalculationUnits.MILE
import data.settings.model.Nationality
import data.settings.model.Nationality.JAP
import domain.calculator.model.ExpectancyCalculationParams
import kotlinx.android.synthetic.main.activity_calculator.helpButton
import kotlinx.android.synthetic.main.activity_calculator.resultView
import kotlinx.android.synthetic.main.activity_calculator.toolbar
import kotlinx.android.synthetic.main.content_calculator.frtDecrement
import kotlinx.android.synthetic.main.content_calculator.iutDecrement
import kotlinx.android.synthetic.main.content_calculator.jfgSeekBar
import kotlinx.android.synthetic.main.content_calculator.jkgSeekBar
import kotlinx.android.synthetic.main.content_calculator.kagValue
import kotlinx.android.synthetic.main.content_calculator.klmEditText
import kotlinx.android.synthetic.main.content_calculator.kpaValue
import kotlinx.android.synthetic.main.content_calculator.lkmIncrement
import kotlinx.android.synthetic.main.content_calculator.nationalityRadioGroup
import kotlinx.android.synthetic.main.content_calculator.resetButton
import kotlinx.android.synthetic.main.content_calculator.saltDecrement
import kotlinx.android.synthetic.main.content_calculator.saltIncrement
import kotlinx.android.synthetic.main.content_calculator.saltSeekBar
import kotlinx.android.synthetic.main.content_calculator.sdfIncrement
import kotlinx.android.synthetic.main.content_calculator.tnz
import kotlinx.android.synthetic.main.content_calculator.ttrEditText
import kotlinx.android.synthetic.main.content_calculator.wegValue
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk15.listeners.onClick
import org.jetbrains.anko.sdk15.listeners.onFocusChange
import org.jetbrains.anko.sdk15.listeners.onLongClick
import org.jetbrains.anko.sdk15.listeners.onTouch
import org.jetbrains.anko.sdk15.listeners.textChangedListener
import org.jetbrains.anko.toast
import presentation.calculator.expectancy.CalculateExpectancyContract
import ui.help.HelpActivity
import ui.injection.scope.PerActivity
import ui.injector
import ui.util.SeekBarButtonType
import ui.util.copyToClipboard
import ui.util.getInt
import ui.util.showSoftKeyboard
import ui.widget.ParameterFieldFilter
import ui.widget.ParameterFieldFilterV26
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@PerActivity
class CalculateExpectancyActivity : AppCompatActivity(), CalculateExpectancyContract.View {

  companion object {
    // Values used for SeekBar animations
    @JvmField val OVERSHOOT = OvershootInterpolator()
    private const val THUMB_PRESS_SCALE_FACTOR = 4000
    private const val THUMB_RELEASE_SCALE_FACTOR = 2
    private const val THUMB_SCALE_DURATION = 600.toLong()
  }

  @Inject lateinit var presenter: CalculateExpectancyContract.Presenter
  private lateinit var unitsPopup: PopupMenu
  private var nationality: Nationality = JAP
  private var calculationUnits: CalculationUnits = MILE
  private var expectancyResult: BigDecimal = BigDecimal.ZERO
  private var expectancyParams: ExpectancyCalculationParams = ExpectancyCalculationParams()
  private val decimalFormatSymbols: DecimalFormatSymbols =
    DecimalFormatSymbols.getInstance(Locale.ENGLISH)
  private val twoDecPlacesFormat: DecimalFormat =
    DecimalFormat("#.##", decimalFormatSymbols)
  private val fourDecPlacesFormat: DecimalFormat =
    DecimalFormat("#.####", decimalFormatSymbols)
  private val seekBarLongClickListeners: List<SeekBarButtonsLongClickListener> = emptyList()
  private var isBeingRestored = false
  private var isWelcomeDialogShown = false

  override fun showCalculationParams(params: ExpectancyCalculationParams) {
    expectancyParams = params
    tnz.setExpectancyParam(params.ggb)
    klmEditText.setExpectancyParam(params.tnz)
    ttrEditText.setExpectancyParam(params.klm)
    jkgSeekBar.setExpectancyParam(
        param = params.aab.multiply(100.toBigDecimal()),
        seekBarMin = getInt(R.integer.aad_min)
    )
    saltSeekBar.setExpectancyParam(
        param = params.rrt.multiply(100.toBigDecimal()),
        seekBarMin = getInt(R.integer.mdb_min)
    )
    jfgSeekBar.setExpectancyParam(
        param = params.oip,
        seekBarMin = getInt(R.integer.bpa_min)
    )
  }

  override fun setNationality(
    nationality: Nationality?,
    notifyListener: Boolean
  ) {
    nationality?.let {
      this.nationality = nationality
      setTextFieldsNationality()
      setUnitPopupNationality()
      nationalityRadioGroup.check(nationality.uiId, notifyListener)
      showResult(expectancyResult)
    }
  }

  override fun setCalculationUnits(calculationUnits: CalculationUnits?) {
    calculationUnits?.let {
      this.calculationUnits = calculationUnits
      showResult(expectancyResult)
    }
  }

  override fun showWelcomeDialog() {
    if (!isWelcomeDialogShown) {
      val dialogContent = SpannableString(getString(R.string.welcome_dialog_content))
      Linkify.addLinks(dialogContent, Linkify.WEB_URLS)
      MaterialDialog.Builder(this)
          .title(R.string.welcome_dialog_title)
          .content(dialogContent)
          .positiveText(R.string.welcome_dialog_ok)
          .dismissListener {
            presenter.saveFirstOpening(false)
            isWelcomeDialogShown = false
          }
          .show()
      isWelcomeDialogShown = true
    }
  }

  override fun showResult(result: BigDecimal) {
    expectancyResult = result
    resultView.text = calculationUnits.formatResult(result)
  }

  override fun showCalculationError() {
    contentView?.longSnackbar(getString(R.string.expectancy_calculation_error))
  }

  override fun showCacheLoadingError() {
    contentView?.longSnackbar(getString(R.string.expectancy_cache_loading_error))
  }

  override fun showSettingsSavingError() {
    contentView?.longSnackbar(getString(R.string.expectancy_settings_saving_error))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injectActivityComponent()
    setContentView(R.layout.activity_calculator)
    setupToolbar()
    setupTextFields()
    setupSeekBars()
    setupSeekBarButtons()
    setupNationalitySelectorListener()
    setupResetButton()
  }

  override fun onStart() {
    super.onStart()
    presenter.start()
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    isBeingRestored = true
    super.onRestoreInstanceState(savedInstanceState)
    isBeingRestored = false
  }

  private fun injectActivityComponent() {
    injector
        .calculatorComponentBuilder()
        .activity(this)
        .build()
        .inject(this)
  }

  private fun setupToolbar() {
    setSupportActionBar(toolbar)
    setUnitDropDownIcon()
    setTooltips()
    setClickListeners()
  }

  private fun setTooltips() {
    TooltipCompat.setTooltipText(helpButton, getString(R.string.tooltip_help))
  }

  private fun setUnitDropDownIcon() {
    val dropDownIcon = ContextCompat.getDrawable(
        this,
        R.drawable.vd_arrow_drop_down_white_24dp
    )
    resultView.setCompoundDrawablesWithIntrinsicBounds(
        null,
        null,
        dropDownIcon,
        null
    )
  }

  private fun setClickListeners() {
    resultView.onLongClick {
      copyResultToClipboard()
      true
    }
    resultView.onClick { showUnitMenu() }
    helpButton.onClick { goToHelpScreen() }
  }

  private fun copyResultToClipboard() {
    resultView.text.copyToClipboard(this)
    toast(R.string.toast_copied_result_to_clipboard)
  }

  private fun showUnitMenu() {
    if (!this::unitsPopup.isInitialized) {
      unitsPopup = PopupMenu(this, resultView)
      with(unitsPopup) {
        inflate(R.menu.result_units_menu)
        val activeItem = calculationUnits.menuId
        menu.findItem(activeItem)?.isChecked = true
        setUnitPopupNationality()
        setOnMenuItemClickListener {
          it.isChecked = true
          setCalculationUnits(
              when (it.itemId) {
                R.id.mileHour -> MILE
                R.id.kilometerHour -> KILOMETER
                else -> throw IllegalArgumentException(
                    "This menu item id is not supported by this listener"
                )
              }
          )
          presenter.saveCalculationUnits(calculationUnits)
          true
        }
      }
    }
    unitsPopup.show()
  }

  private fun setUnitPopupNationality() {
    if (this::unitsPopup.isInitialized) {
      with(unitsPopup.menu) {
        for (i in 0 until size()) {
          getItem(i).title = when (getItem(i).itemId) {
            R.id.mileHour -> CalculationUnits.MILE.getMenuDescription()
            R.id.kilometerHour -> CalculationUnits.KILOMETER.getMenuDescription()
            else -> throw IllegalArgumentException(
                "This menu item id is not supported by this listener"
            )
          }
        }
      }
    }
  }

  private fun goToHelpScreen() {
    startActivity(intentFor<HelpActivity>())
  }

  private fun setupTextFields() {
    setTextFieldsFilters()
    setTextFieldsListeners()
  }

  private fun setTextFieldsListeners() {
    setListenerForTextField(
        editText = tnz,
        updateFunction = { expectancyParams.ggb = tnz.textAsBigDecimal() }
    )
    setListenerForTextField(
        editText = klmEditText,
        updateFunction = { expectancyParams.tnz = klmEditText.textAsBigDecimal() }
    )
    setListenerForTextField(
        editText = ttrEditText,
        updateFunction = { expectancyParams.klm = ttrEditText.textAsBigDecimal() }
    )
  }

  private fun setListenerForTextField(
    editText: EditText,
    updateFunction: () -> Unit
  ) {
    with(editText) {
      textChangedListener {
        onTextChanged { _, _, _, _ ->
          if (text.isEmpty()) {
            setHint(R.string.field_hint)
          } else {
            hint = ""
          }
        }
        afterTextChanged {
          if (tag == null && !isBeingRestored) {
            recalculateExpectancy(updateFunction)
          }
        }
      }
      onFocusChange { _, hasFocus ->
        if (hasFocus) {
          showSoftKeyboard()
        } else if (!hasFocus && text.isNotEmpty()) {
          formatParameterInput(textAsDouble())
        }
      }
    }
  }

  private fun setTextFieldsFilters() {
    val parameterFieldListener = if (VERSION.SDK_INT < VERSION_CODES.O) {
      ParameterFieldFilter()
    } else {
      ParameterFieldFilterV26()
    }
    val filters = arrayOf(parameterFieldListener, LengthFilter(8))
    tnz.filters = filters
    klmEditText.filters = filters
    ttrEditText.filters = filters
  }

  private fun setTextFieldsNationality() {
    klmEditText.setPrefix(nationality.getSign())
    ttrEditText.setPrefix(nationality.getSign())
  }

  private fun setupSeekBars() {
    jkgSeekBar.max =
        getInt(R.integer.aad_max) - getInt(R.integer.aad_min)
    jfgSeekBar.max =
        getInt(R.integer.bpa_max) - getInt(R.integer.bpa_min)
    saltSeekBar.max =
        getInt(R.integer.mdb_max) - getInt(R.integer.mdb_min)
    setSeekBarListener(jkgSeekBar)
    setSeekBarListener(jfgSeekBar)
    setSeekBarListener(saltSeekBar)
  }

  private fun setSeekBarListener(seekBar: SeekBar) {
    val thumbDrawable = ContextCompat.getDrawable(this, R.drawable.ll_thumb)
    with(seekBar) {
      thumbDrawable?.let { thumb = it }
      thumb.level = 1
      val seekBarChangeListener = ParameterSeekBarListener()
      setOnSeekBarChangeListener(seekBarChangeListener)
      initSeekBarButtonsLongClickListener()
    }
  }

  private inner class ParameterSeekBarListener : OnSeekBarChangeListener {

    private var isUpdateLocked = false

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
      seekBar?.let {
        animateThumbRelease(it)
        if (isUpdateLocked) {
          updateParameterValue(
              seekBar = seekBar,
              progress = seekBar.progress,
              recalculateExpectancy = true
          )
        }
      }
      isUpdateLocked = false
    }

    override fun onProgressChanged(
      seekBar: SeekBar,
      progress: Int,
      fromUser: Boolean
    ) {
      if (fromUser) {
        enlargeSeekBarThumb(seekBar)
      }
      updateParameterValue(
          seekBar = seekBar,
          progress = progress,
          recalculateExpectancy = fromUser && !isUpdateLocked
      )
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
      isUpdateLocked = true
    }

  }

  private fun animateThumbRelease(seekBar: SeekBar) {
    val thumb = seekBar.thumb
    val initLevel = thumb.level
    val maxLevel = thumb.level * THUMB_RELEASE_SCALE_FACTOR
    val endLevel = 1
    val animator = ValueAnimator.ofInt(initLevel, maxLevel, endLevel)

    with(animator) {
      interpolator = OVERSHOOT
      duration = THUMB_SCALE_DURATION
      addUpdateListener {
        thumb.level = it.animatedValue as Int
      }
      start()
    }
  }

  private fun enlargeSeekBarThumb(seekBar: SeekBar) {
    seekBar.thumb.level = THUMB_PRESS_SCALE_FACTOR
  }

  private fun updateParameterValue(
    seekBar: SeekBar,
    progress: Int,
    recalculateExpectancy: Boolean
  ) {
    when (seekBar.id) {
      R.id.jkgSeekBar -> {
        val value = progress + getInt(R.integer.aad_min)
        kpaValue.setPercentText(value)
        if (recalculateExpectancy)
          recalculateExpectancy { expectancyParams.aab = (value / 100.0).toBigDecimal() }
      }
      R.id.saltSeekBar -> {
        val value = progress + getInt(R.integer.mdb_min)
        wegValue.setPercentText(value)
        if (recalculateExpectancy)
          recalculateExpectancy { expectancyParams.rrt = (value / 100.0).toBigDecimal() }
      }
      R.id.jfgSeekBar -> {
        val value = progress + getInt(R.integer.bpa_min)
        kagValue.setYearsText(value)
        if (recalculateExpectancy)
          recalculateExpectancy { expectancyParams.oip = value.toBigDecimal() }
      }
      else -> throw IllegalArgumentException("This view id is not supported by this listener")
    }
  }

  private fun SeekBar.initSeekBarButtonsLongClickListener() {
    seekBarLongClickListeners.plus(SeekBarButtonsLongClickListener(this))
  }

  private fun setupSeekBarButtons() {
    lkmIncrement.onClick {
      handleSeekBarButtonClick(jkgSeekBar, SeekBarButtonType.INCREMENT)
    }
    iutDecrement.onClick {
      handleSeekBarButtonClick(jkgSeekBar, SeekBarButtonType.DECREMENT)
    }
    saltIncrement.onClick {
      handleSeekBarButtonClick(saltSeekBar, SeekBarButtonType.INCREMENT)
    }
    saltDecrement.onClick {
      handleSeekBarButtonClick(saltSeekBar, SeekBarButtonType.DECREMENT)
    }
    sdfIncrement.onClick {
      handleSeekBarButtonClick(jfgSeekBar, SeekBarButtonType.INCREMENT)
    }
    frtDecrement.onClick {
      handleSeekBarButtonClick(jfgSeekBar, SeekBarButtonType.DECREMENT)
    }
  }

  private fun handleSeekBarButtonClick(
    seekBar: SeekBar,
    buttonType: Int
  ) {
    with(seekBar) {
      if (buttonType == SeekBarButtonType.INCREMENT) {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
          setProgress(progress + 1, true)
        } else {
          progress++
        }
      } else {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
          setProgress(progress - 1, true)
        } else {
          progress--
        }
      }
      updateParameterValue(
          seekBar = this,
          progress = progress,
          recalculateExpectancy = true
      )
    }
  }

  private fun recalculateExpectancy(updateFunction: () -> Unit) {
    run(updateFunction)
    presenter.calculateExpectancy(expectancyParams)
  }

  private fun setupNationalitySelectorListener() {
    nationalityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      setNationality(Nationality.fromUiId(checkedId), true)
      presenter.saveNationality(nationality)
    }
  }

  private fun setupResetButton() {
    resetButton.onClick {
      showCalculationParams(ExpectancyCalculationParams())
      presenter.calculateExpectancy(expectancyParams)
    }
  }

  private inner class SeekBarButtonsLongClickListener(
    private val seekBar: SeekBar
  ) {

    private val updateDelay = 100.toLong()
    private val handler: Handler = Handler()
    private var autoIncrement = false
    private var autoDecrement = false
    private var incrementButton: View? = null
    private var decrementButton: View? = null

    init {
      initButtons()
      setButtonLongClickListeners(SeekBarButtonType.DECREMENT)
      setButtonLongClickListeners(SeekBarButtonType.INCREMENT)
    }

    private fun initButtons() {
      when (seekBar.id) {
        R.id.jkgSeekBar -> {
          incrementButton = lkmIncrement
          decrementButton = iutDecrement
        }
        R.id.saltSeekBar -> {
          incrementButton = saltIncrement
          decrementButton = saltDecrement
        }
        R.id.jfgSeekBar -> {
          incrementButton = sdfIncrement
          decrementButton = frtDecrement
        }
        else -> throw IllegalArgumentException("This SeekBar id is not supported by this listener")
      }
    }

    private fun setButtonLongClickListeners(@SeekBarButtonType buttonType: Int) {
      val isIncrement = buttonType == SeekBarButtonType.INCREMENT
      val button = if (isIncrement) incrementButton else decrementButton

      button?.onLongClick {
        if (isIncrement) {
          autoIncrement = true
        } else {
          autoDecrement = true
        }
        handler.post(ValueUpdater(this))
        false
      }

      button?.onTouch { _, event ->
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
          if (isIncrement) {
            autoIncrement = false
          } else {
            autoDecrement = false
          }
        }
        false
      }
    }

    private inner class ValueUpdater(
      private val seekBarButtonsListener: SeekBarButtonsLongClickListener
    ) : Runnable {
      override fun run() {
        seekBarButtonsListener.apply {
          if (autoIncrement) {
            handleSeekBarButtonClick(seekBar, SeekBarButtonType.INCREMENT)
            handler.postDelayed(ValueUpdater(this), updateDelay)
          } else if (autoDecrement) {
            handleSeekBarButtonClick(seekBar, SeekBarButtonType.DECREMENT)
            handler.postDelayed(ValueUpdater(this), updateDelay)
          }
        }
      }
    }
  }

  /*
      EXTENSION FUNCTIONS
   */

  /**
   * Extension function for [TextView] to set text from [R.string.expectancy_parameter_percent_value].
   */
  private fun TextView.setPercentText(value: Int) {
    text = resources.getString(R.string.expectancy_parameter_percent_value, value)
  }

  /**
   * Extension function for [TextView] to set text from [R.plurals.expectancy_parameter_years_value].
   */
  private fun TextView.setYearsText(years: Int) {
    text = resources.getQuantityString(R.plurals.expectancy_parameter_years_value, years, years)
  }

  private fun SeekBar.setExpectancyParam(
    param: BigDecimal,
    seekBarMin: Int
  ) {
    val paramInt = param.toInt()
    if (paramInt >= seekBarMin) {
      val newProgress = paramInt - seekBarMin
      if (progress == newProgress) {
        updateParameterValue(
            seekBar = this,
            progress = newProgress,
            recalculateExpectancy = false
        )
      } else {
        progress = newProgress
      }
    }
  }

  private fun EditText.setExpectancyParam(param: BigDecimal) {
    tag = "manualChange"
    if (param == BigDecimal.ZERO) {
      text.clear()
    } else {
      setText(twoDecPlacesFormat.format(param))
      setSelection(length())
    }
    tag = null
  }

  private fun EditText.formatParameterInput(param: Double) {
    tag = "manualChange"
    setText(twoDecPlacesFormat.format(param))
    setSelection(length())
    tag = null
  }

  private fun EditText.textAsBigDecimal(): BigDecimal {
    return try {
      text.toString().toBigDecimal()
    } catch (e: Exception) {
      BigDecimal.ZERO
    }
  }

  private fun EditText.textAsDouble(): Double {
    return try {
      text.toString().toDouble()
    } catch (e: Exception) {
      0.0
    }
  }

  private fun CalculationUnits.getMenuDescription(): String {
    val nationalityName = if (this == MILE) nationality.signResId else nationality.centesimalNameResId
    return getString(
        R.string.expectancy_result_unit_menu_description,
        getString(nationalityName),
        getString(resultUnitResId)
    )
  }

  private fun CalculationUnits.formatResult(value: BigDecimal): String {
    val result = if (name == KILOMETER.name) value / 1000.toBigDecimal() else value
    val format = if (name == KILOMETER.name) fourDecPlacesFormat else twoDecPlacesFormat
    return getString(
        resultValueResId,
        getString(nationality.signResId),
        format.format(result),
        getString(resultUnitResId)
    )
  }

  private fun Nationality.getSign() = getString(signResId)

  private fun RadioGroup.check(@IdRes id: Int, notifyListener: Boolean) {
    if (!notifyListener) setOnCheckedChangeListener(null)
    check(id)
    if (!notifyListener) setupNationalitySelectorListener()
  }

}