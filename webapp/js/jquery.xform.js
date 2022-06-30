var KEY_MODIFY=77,
KEY_NEW=78,
KEY_REMOVE=82,
KEY_VIEW=86,
KEY_ENTER=13,
KEY_ESCAPE=27;
var INTEGER='INTEGER',
DECIMAL='DECIMAL',
STRING='STRING',
DATE='DATE',
TIME='TIME',
DATETIME='DATETIME',
OBJECT='OBJECT';
var MESSAGE_NOTNULL = "Cannot be empty.",
MESSAGE_MINVALUE = "Value should be greater than or equal to ",
MESSAGE_MAXVALUE = "Value should be less than or equal to ",
MESSAGE_MINLENGTH = "Length of value should be greater than or equal to ",
MESSAGE_MAXLENGTH = "Length of value should be less than or equal to ",
MESSAGE_EQUALLENGTH = "Length of value should be equal to ",
MESSAGE_PATTERN = "Value does not match the pattern. ",
MESSAGE_INTEGERLENGTH = "Number of digits in the integer part of value should not exceed ",
MESSAGE_DECIMALLENGTH = "Number of digits in the decimal part of value should not exceed ",
MESSAGE_FORMAT = "Value does not match the format",
MESSAGE_OTHER = "Unable to set value. Reason ",
MESSAGE_SHOULDBELIST = "List of values expected",
MESSAGE_SHOULDBEINTEGER = "Integer value expected",
MESSAGE_SHOULDBEDECIMAL = "Decimal value expected",
MESSAGE_SHOULDBEDATE = "Date/Time value expected",
MESSAGE_SHOULDBETIME = "Time value expected",
MESSAGE_SHOULDBEDATETIME = "Date Time value expected",
MESSAGE_SHOULDBEBOOLEAN = "Boolean value expected",
MESSAGE_MINITEMS = "Number of items in the list should be greater than or equal to ",
MESSAGE_MAXITEMS = "Number of items in the list should be less than or equal to ";
var FORMAT_DATE = "dd-MM-yyyy",
FORMAT_TIME = "HH:mm:ss",
FORMAT_DATETIME = FORMAT_DATE + " " + FORMAT_TIME,
FORMAT_INTEGER = "##,##,##,##,##,##,##,##,###",
FORMAT_DECIMAL = "##,##,##,##,##,##,##,##0.00##";
var KEYWORD_CURRENTDATE = "currentDate",
KEYWORD_CURRENTTIME = "currentTime",
KEYWORD_CURRENTDATETIME = "currentDateTime";
var INFO='info',SUCCESS='success',WARNING='warning',DANGER='danger';
var OTP_IS_REQUIRED = 418;
var OTP_RETRY = 419;
var PATTERNS = {
};

var dataSetCache = {};
/*
 * Common interface for all fields
 * init() : initialize a field. Can be called multiple times. Will be called 1st time by default
 * getOptions() : returns the configuration options hash.
 * getValue() : returns the value of the field. 
 * setValue() : sets value into the field.
 * disable() : disable a field
 * enable() : enable a field
 * isDisabled() : checks if field is disabled
 * boolean focus() : Sets focus to the field. Returns true is field is focussable 
 * getDisplayValue() : Optional implementation. Gets html when in view mode.
 * isMultiValue() : Optional implementation. Indicates that the control can return a list of values. Hence wrapping with XMultiWrapper not required
 */
/** XFormField **/
!function( $ ) {
	var XFormField = function(element, options) {
		this.$element = $(element);
		this.options = options||this.options||{};
		var lAttribs=['dataType','maxLength','integerLength','decimalLength','allowedChars','blockedChars'];
		for (var lPtr in lAttribs) {
			this.options[lAttribs[lPtr]]=this.options[lAttribs[lPtr]]||this.$element.data(lAttribs[lPtr])||false;
		}
		this.init();
	};
	
	XFormField.prototype = {
		constructor: XFormField,
		init: function() {
			if (this.$element.is('select')) {
				this.isSelect = true;
				var lElement$ = this.$element;
				if (this.options.staticLength==null)
					this.options.staticLength=lElement$.find('option').length;
				populateOptions(lElement$,this.options.dataSetValues,this.options.staticLength);
			} else if (this.$element.is('input:radio') || this.$element.is('input:checkbox')) {
				this.isCheck = true;
				var lTpl = this.$element.parent('label');
				var lId = null;
				var lCont = lTpl.parent();
				var lDataSetValues = getDataset(this.options.dataSetValues);
				$.each(lDataSetValues, function(pIndex, pValue) {
					var lFld = pIndex==0?lTpl:lTpl.clone(true);
					var lFldInp = lFld.find('input');
					var lText = pValue, lValue = pValue;
					if (typeof pValue === 'object') {
						lText = pValue.text || pValue.value;
						lValue = pValue.value;
					}
					lFldInp.val(lValue);
					lFld.find('span').text(lText);
					if (pIndex > 0) {
						lFldInp.prop('id', lId + pIndex);
						lCont.append(lFld);
						lFld.find('.tooltip').remove();
					} else
						lId = lFldInp.prop('id');
					lFldInp.prop('name',lId);
				});
				this.$element = $('input[name='+this.$element.prop('name')+']');
			} else if (this.$element.is('input:text') || this.$element.is('input:hidden') || this.$element.is('textarea') || this.$element.is('input:password')) {
				this.isText = true;
				if (this.options.dataType === INTEGER) {
					this.options.allowedChars = "+-0123456789";
				}
				else if (this.options.dataType === DECIMAL) {
					this.options.allowedChars = "+-0123456789.";
				}
				if (this.options.allowedChars || this.options.blockedChars) {
					var lThis=this;
					this.$element.off('keypress').on('keypress',function(e){
						var lCode = parseInt(e.keyCode || e.which);
						if ($.inArray(lCode,[8,9,46,35,36,37,38,39,40]) >= 0) return;
						var lCharCode = String.fromCharCode(lCode);
						if (lThis.options.allowedChars && lThis.options.allowedChars.indexOf(lCharCode) >= 0)
							return;
						if (lThis.options.blockedChars && lThis.options.blockedChars.indexOf(lCharCode) < 0)
							return;
						e.preventDefault();
					});
				}
				if (this.options.conversion) {
					var lThis=this;
					this.$element.off('blur').on('blur',function(e){
						lThis.setValue(applyConversions(lThis.getValue(), lThis.options.conversion));
					});
				}
				var lSize=null;
				if (this.options.dataType === STRING)
					lSize = this.options.maxLength;
				else if (this.options.dataType === INTEGER)
					lSize = this.options.integerLength;
				else if (this.options.dataType === DECIMAL) {
					lSize = this.options.integerLength;
					if (lSize && this.options.decimalLength)
						lSize += this.options.decimalLength + 1;
				}
				if (lSize)
					this.$element.prop('maxLength', lSize);
			}
		},
		getOptions: function() {
			return this.options;
		},
		getValue: function() {
			if (this.isText || this.isSelect) {
				var lVal = this.$element.val();
				if (lVal == "") {
					// allow blanks only in text fields where allowblank is true
					if (!(this.isText && this.options.allowBlank))
						lVal = null;
				}
				return lVal;
			} else if (this.isCheck) {
				if (this.options.allowMultiple) {
					var lVal=[];
					this.$element.filter(':checked').each(function(){
						lVal.push($(this).val());
					});
					return lVal;
				} else {
					return this.$element.filter(':checked').val();
				}

			}
			return null;
		},
		setValue: function(pValue) {
			if (this.isText)
				this.$element.val(pValue);
			else if (this.isSelect) {
				this.$element.val(pValue);
				if (this.$element.prop('selectedIndex') < 0)
					this.$element.prop('selectedIndex', 0);
			}
			else if (this.isCheck) {
				this.$element.val(this.options.allowMultiple?pValue:[pValue]);
			}
		},
		disable: function() {
			this.$element.prop('disabled',true);
		},
		enable: function() {
			this.$element.prop('disabled',false);
		},
		isDisabled: function() {
			return this.$element.prop('disabled');
		},
		focus: function() {
			if (this.$element.is('input:hidden')) return false;
			this.$element.focus();
			return true;
		},
		isMultiValue : function(){
			return this.isCheck;
		}
	};
	$.fn.xformfield = function(option, val) {
		return this.each(function() {
			var $this = $(this),
			data = $this.data('xformfield'),
			options = typeof option === 'object' && option;
			if (!data) {
				data = new XFormField(this, $.extend({},$.fn.xformfield.defaults,options));
				$this.data('xformfield', data);
			}
			if (typeof option === 'string') data[option](val);
		});
	};
}( window.jQuery );

/** XUploadField **/
!function( $ ) {
	var XUploadField = function(element, options) {
		this.$element = $(element);
		this.options = options||this.options||{};
		var lAttribs=['fileType'];
		for (var lPtr in lAttribs) {
			this.options[lAttribs[lPtr]]=this.options[lAttribs[lPtr]]||this.$element.data(lAttribs[lPtr])||false;
		}
		this.options['isUploading'] = false;// flag to indicate upload in progress
		// dummy field for upload
		var lThis=this;

		var lFieldName = 'upl-'+Math.floor(Math.random()*100000000 + 1)+'-'+this.options.name;
		this.$uploadField = $('#'+lFieldName);
		if (this.$uploadField.length==0){
			$(document.body).append(
				$('<div />',{style:'width:0px;height:0px;overflow:hidden'}).append(
					$('<input />', { id: lFieldName, type: 'file' })
				)
			);
			this.$uploadField = $('#'+lFieldName);
			this.$uploadField.on('change', function(pEvent){
				if (this.files.length==1) {
					var lData = new FormData();
					lData.append("filecontent",this.files[0]);
					lThis.$uploading.show();
					lThis.options.isUploading = true;
					$.ajax({
						url:'upload/'+lThis.options.fileType,
						data: lData,
					    cache: false,
					    contentType: false,
					    processData: false,
					    timeout: 300000,
					    type: 'POST',
					    success: function(data){
					    	lThis.setValue(data.fileName);
					    },
					    error: errorHandler,
					    complete: function() {
					    	lThis.$uploading.hide();
					    	lThis.options.isUploading=false;
					    }
					});
				}
			});
		}
		//
		var lParent$=this.$element.parent();
		this.$btnUpload=lParent$.find('.upl-btn-upload');
		this.$btnClear=lParent$.find('.upl-btn-clear');
		this.$imgPreview=lParent$.find('.upl-img-preview');
		this.$info=lParent$.find('.upl-info');
		this.$uploading=lParent$.find('.upl-uploading');
		this.disabled=false;
		this.$btnUpload.on('click', function(pEvent){
			lThis.$uploadField.val(null);
			lThis.$uploadField.trigger('click');
		});
		this.$btnClear.on('click', function(pEvent){
			lThis.setValue(null);
		});
		this.init();
	};
	
	XUploadField.prototype = {
		constructor: XUploadField,
		init: function() {
		},
		getOptions: function() {
			return this.options;
		},
		getValue: function() {
			var lVal = this.$element.val();
			return lVal;
		},
		setValue: function(pValue) {
			this.$element.val(pValue);
			var lEmpty=isNullOrEmpty(pValue);
			var lLoginKey = loginData==null?null:loginData.loginKey;
			var lUrl = 'upload/'+this.options.fileType+'/'+pValue+'?loginKey='+lLoginKey;
			if (this.$imgPreview.length>0) {
				if (lEmpty) {
					this.$imgPreview.hide();
					this.$imgPreview.attr('src','');
				} else {
					this.$imgPreview.show();
					this.$imgPreview.attr('src',lUrl);
				}
			}
			if (this.$info.length>0) {
				if (lEmpty) {
					this.$info.hide();
					this.$info.html('');
				} else {
					this.$info.show();
					var lPos=pValue.indexOf('.');
					if (this.$imgPreview.length > 0){
						this.$info.text(lPos>0?pValue.substring(lPos+1):pValue);
					}
					else{
						var lFileNameTmp = htmlEscape((lPos>0?pValue.substring(lPos+1):pValue));
						if (!lFileNameTmp.match(/.(jpg|jpeg|png|gif|tiff|bmp)$/i)) {
							this.$info.html("<a href='javascript:void' onClick=\"javascript:window.open('"+lUrl+"')\">"+lFileNameTmp+"</a>");
						} else {
							this.$info.html("<a href='javascript:void' onClick=\"javascript:alert('<a href=\\'"+lUrl+"\\' class=\\'btn btn-sm btn-primary\\'><span class=\\'fa fa-download\\'></span> Download</a><hr><center><img src=\\'"+lUrl+"\\' ></center>','Attachment')\">"+lFileNameTmp+"</a>");
						}
					}
				}
			}
		},
		disable: function() {
			this.disabled=true;
			this.$btnUpload.prop('disabled',true);
			this.$btnClear.prop('disabled',true);
		},
		enable: function() {
			this.disabled=false;
			this.$btnUpload.prop('disabled',false);
			this.$btnClear.prop('disabled',false);
		},
		isDisabled: function() {
			return this.disabled;
		},
		focus: function() {
			this.$element.focus();
			return true;
		},
		getDisplayValue: function() {
			var lEmpty=isNullOrEmpty(this.getValue());
			if (lEmpty) return "";
			else {
				var lHtml = "";
				if (this.$info.length>0) lHtml += this.$info[0].outerHTML;
				if (this.$imgPreview.length>0) lHtml += this.$imgPreview[0].outerHTML;
				return lHtml;
			}
		}
	};
	$.fn.xuploadfield = function(option, val) {
		return this.each(function() {
			var $this = $(this),
			data = $this.data('xuploadfield'),
			options = typeof option === 'object' && option;
			if (!data) {
				data = new XUploadField(this, $.extend({},$.fn.xuploadfield.defaults,options));
				$this.data('xuploadfield', data);
			}
			if (typeof option === 'string') data[option](val);
		});
	};
}( window.jQuery );


/** XForm **/
!function( $ ) {
	var XForm = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.mode = null;
		this.method = null;
		this.formData = null;
		this.fields = null;
		this.fieldMap = null;//key=fieldname, value=fieldwrapperobj
		this.draftMode=false;// true=NOT_NULL check bypassed
		this.disabled=false;
		this.init();
	};
	
	XForm.prototype = {
		constructor: XForm,
		init: function() {
			this.fields = [];
			this.fieldMap = {};
			var lOptions = this.options;
			var lThis=this;
			$.each(lOptions.fields, function(pIndex, pValue) {
				lThis.initField(pValue);
			});
		},
		initField: function(pFldOptions) {
			var lElement$ = this.$element;
			var lOptions = this.options;
			var lParent = (lOptions.parent?lOptions.parent+"-":"");
			var lField$ = lElement$.find('#'+lParent+pFldOptions.name);
				if (lField$.length == 0) {
				this.fields.push({"name":pFldOptions.name});
					return;
				}
				pFldOptions.dataAttributes = lField$.data();
				var lContainer$ = lField$.closest('section');
				var lToolTip = lContainer$.find(".tooltip");
				if (lToolTip.length > 0) {
				lToolTip.append(pFldOptions.desc?pFldOptions.desc:(pFldOptions.label?pFldOptions.label:pFldOptions.name));
				if (pFldOptions.patternMessage)
					lToolTip.append('<br>'+pFldOptions.patternMessage);
					lToolTip.append($('<ul></ul>'));
				}
				var lErr$ = lContainer$.find('ul');
				var lFieldObj = null
				var lRole = lField$.data('role');
				if (!lRole) {
				if (pFldOptions.dataType == OBJECT) {
					pFldOptions.parent = lParent + pFldOptions.name;
					if (pFldOptions.allowMultiple) {
							lRole = 'xcrudwrapper';
						pFldOptions.autoRefresh = true;
						} else lRole = 'xform';
					} else lRole = 'xformfield';
				}
			if (lField$[lRole]) lField$[lRole](pFldOptions);
				else oldAlert("Control not found " + lRole);
				lFieldObj = lField$.data(lRole);
				// wrap multi scalar fields !lRole && 
				var lMultiValControl = (lFieldObj.isMultiValue && lFieldObj.isMultiValue());
			if (!lMultiValControl && (pFldOptions.dataType != OBJECT) && pFldOptions.allowMultiple && !lField$.data('noMultiWrap'))
					lFieldObj = new XMultiWrapper(lFieldObj, lContainer$);

			if (pFldOptions.notNull && !lOptions.forFilter)
					lContainer$.addClass('state-mandatory');
				var lViewContainer$ = lContainer$.siblings('section.view');
			var lFldWrapper = {"name":pFldOptions.name, "fldObj": lFieldObj, "$field": lField$, "$container": lContainer$, "$viewContainer": lViewContainer$, "$error": lErr$}; 
			this.fields.push(lFldWrapper);
			this.fieldMap[pFldOptions.name] = lFldWrapper;
		},
		getOptions: function() {
			return this.options;
		},
		getValue: function(pSkipNulls) {
			var lValue={};
			var lFormData = this.formData;
			$.each(this.fields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				var lVal = lFieldObj?lFieldObj.getValue():(lFormData?lFormData[pValue.name]:null);
				if ((lVal!=null) || !pSkipNulls)
					lValue[pValue.name] = lVal;
			});
			return lValue;
		},
		setValue: function(pVal, pDefault) {
			$.each(this.fields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				if (lFieldObj) {
					var lVal=pVal==null?null:pVal[pValue.name];
					if (pDefault && (lVal==null)) lVal=lFieldObj.getOptions().defaultValue;
					lFieldObj.setValue(lVal);
					if (pValue.$viewContainer) {
						var lDispVal = lFieldObj.getDisplayValue?lFieldObj.getDisplayValue():getDisplayValue(lFieldObj);
						if (lDispVal==null) lDispVal="";
						//lFieldObj.getDisplayValue?pValue.$viewContainer.text(lDispVal):pValue.$viewContainer.html(lDispVal);
						pValue.$viewContainer.html(lDispVal);
					}
				}
				if (pValue.$error) pValue.$error.empty();
				if (pValue.$container) pValue.$container.removeClass('state-error');
			});
			this.formData=pVal;
		},
		disable: function() {
			$.each(this.fields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				if (lFieldObj) lFieldObj.disable();
			});
			this.disabled=true;
		},
		enable: function() {
			$.each(this.fields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				if (lFieldObj) lFieldObj.enable();
			});
			this.disabled=false;
		},
		enableDisableField: function(pName, pEnable, pClear) {
			var lNames = $.isArray(pName)?pName:[pName];
			var lFieldMap = this.fieldMap;
			$.each(lNames, function (pIndex, pName){
				var lFldWrapper = lFieldMap[pName];
				if (lFldWrapper) {
					if (lFldWrapper.fldObj) {
						if (pClear) lFldWrapper.fldObj.setValue(null);
						if (pEnable) lFldWrapper.fldObj.enable();
						else lFldWrapper.fldObj.disable();
					}
					if (lFldWrapper.$container) {
						if (pEnable) lFldWrapper.$container.removeClass('state-disabled');
						else lFldWrapper.$container.addClass('state-disabled');
					}
				}
			});
		},
		alterField: function(pName, pNotNull, pAllowBlank) {
			var lNames = $.isArray(pName)?pName:[pName];
			var lFieldMap = this.fieldMap;
			var lForFilter = this.options.forFilter;
			$.each(lNames, function (pIndex, pName){
				var lFldWrapper = lFieldMap[pName];
				if (lFldWrapper) {
					if (lFldWrapper.fldObj) {
						var lOptions = lFldWrapper.fldObj.getOptions(); 
						lOptions.notNull = pNotNull;
						lOptions.allowBlank = pAllowBlank;
					}
					if (lFldWrapper.$container && !lForFilter) {
						if (pNotNull)
							lFldWrapper.$container.addClass('state-mandatory');
						else
							lFldWrapper.$container.removeClass('state-mandatory');
					}
				}
			});
		},
		isDisabled: function() {
			/*var lDisabled = true;
			$.each(this.fields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				if (lFieldObj && !lFieldObj.isDisabled()) lDisabled = false;
				if (lFieldObj) console.log(pValue.name + ":" + lFieldObj.isDisabled());
			});
			return lDisabled;*/
			return this.disabled;
		},
		focus: function() {
			var lFocusField = this.$element.find('.focusField');
			if (lFocusField.length > 0) {
				lFocusField.focus();
				return true;
			}
			var lFields;
			if (this.mode) {
				lFields=[];
				var lFieldMap=this.fieldMap;
				$.each(this.options.fieldGroups[this.mode],function(pIndex,pValue){
					if (lFieldMap[pValue])
						lFields.push(lFieldMap[pValue]);
				});
			}
			if (!lFields) lFields = this.fields;
			var lFocussed = false;
			$.each(lFields, function(pIndex, pValue){
				var lFieldObj = pValue.fldObj;
				if (lFieldObj && !lFieldObj.isDisabled()) {
					if (lFieldObj.focus()) {
						lFocussed = true;
						return false;
					}
				}
			});
			return lFocussed;
		},
		setViewMode: function(pShow) {
			if (pShow) this.$element.addClass('view');
			else this.$element.removeClass('view');
		},
		setMode: function(pMode) {
			this.mode = pMode;
			var lFields;
			if (this.mode) {
				lFields={};
				$.each(this.options.fieldGroups[this.mode],function(pIndex,pValue){
					lFields[pValue] = true;
				});
			}
			this.method = this.mode?(this.mode=="insert"?"POST":"PUT"):null;
			var lModify=this.method==="PUT";
			$.each(this.fields, function(pIndex, pValue){
				if (!pValue.fldObj) return; 
				var lDisabled = lFields && !lFields[pValue.name];
				if (lModify && (pValue.fldObj.getOptions().fieldType==="PRIMARY"))
					lDisabled=true;
				if (lDisabled) {
					if (!pValue.fldObj.isDisabled()) pValue.fldObj.disable();
					pValue.$container.addClass('state-disabled');
				} else {
					if (pValue.fldObj.isDisabled()) pValue.fldObj.enable();
					pValue.$container.removeClass('state-disabled');
				}
			});
		},
		getMode:function() {
			return this.mode;
		},
		setDraftMode: function(pDraftMode) {
			this.draftMode=pDraftMode;
		},
		check: function() {
			var lThis = this;
			var lAllErrors = [];
			var lFields;
			if (this.mode) {
				lFields = {};
				$.each(this.options.fieldGroups[this.mode],function(pIndex,pValue){
					lFields[pValue] = true;
				});
			}
			$.each(this.fields, function(pIndex, pValue){
				var lFldOptions = pValue;
				if (lFields && !lFields[lFldOptions.name]) return;
				var lFieldObj = lFldOptions.fldObj;
				if (!lFieldObj) return;
				lFldOptions.$error.empty();
				// check
				var lErrors = null;
				if (lFieldObj.getOptions().dataType == OBJECT) {
					lErrors = lFieldObj.check();
				} else {
					lErrors = validate(lFieldObj.getValue(), lFieldObj.getOptions(), lThis.draftMode);
					if (lFieldObj.getOptions().checkHandler) {
						var lAddnlErrors = lFieldObj.getOptions().checkHandler(lFieldObj.getValue(), lFieldObj.getOptions());
						if ((lAddnlErrors != null) && (lAddnlErrors.length>0))
							lErrors = lErrors?lErrors.concat(lAddnlErrors):lAddnlErrors;
					}
				}
				if (lErrors != null && lErrors.length > 0) {
					lAllErrors = lAllErrors.concat(lErrors);
					lFldOptions.$container.addClass('state-error');
					$.each(lErrors, function(pErrIndex, pErrValue) {
						lFldOptions.$error.append($('<li>').append(pErrValue));
					});
					lFldOptions["errors"] = lErrors;
				} else {
					lFldOptions.$container.removeClass('state-error');
					lFldOptions["errors"] = null;
				}
			});
			return lAllErrors;
		},
		isChanged: function() {
			return !compareJson(this.getValue(),this.formData);
		},
		getField: function (pField, pLevel) {
			var lFldObj = null;
			var lField = null;
			if (!pLevel) pLevel=0;
			var lSplits = pField.split('-');
			if (pLevel < lSplits.length) 
				lField = lSplits[pLevel];
			else 
				lField = pField;
			var lFldWrapper = this.fieldMap[lField];
			if (lFldWrapper) {
				lFldObj = lFldWrapper.fldObj;
				if ((lFldObj instanceof XForm) && (pLevel < lSplits.length-1))
						lFldObj = lFldObj.getField(pField, pLevel+1);
				}
			return lFldObj;
		}
	};
	
	// jquery plugin to wrap the xform object
	$.fn.xform = function(option, val) {
		return this.each(function() {
			var $this = $(this),
			data = $this.data('xform'),
			options = typeof option === 'object' && option;
			if (!data) {
				data = new XForm(this, $.extend(true,{},$.fn.xform.defaults,options));
				$this.data('xform', data);
			}
			if (typeof option === 'string') data[option](val);
		});
	};
	
	/** XMultiWrapper **/
	var XMultiWrapper = function(pBaseField, pContainer$) {
		this.baseField = pBaseField;
		this.valueList = null;
		this.$container = pContainer$;
		this.$listElement=this.$container.find('.value-list');
		var lThis=this;
		this.$container.find('button').on('click',function(){
			var lVal=lThis.baseField.getValue();
			if ((lVal==null)||(lVal=='')) return;
			if (lThis.valueList==null||lThis.valueList=='') lThis.valueList=[];
			var lErrors = validate(lVal, lThis.getOptions());
			if (lThis.getOptions().maxItems && (lThis.valueList.length >= lThis.getOptions().maxItems))
				lErrors.push(MESSAGE_MAXITEMS + lThis.getOptions().maxItems);
			if (lErrors != null && lErrors.length > 0) {
				alert(lErrors[0],null,function(){lThis.focus();});
			} else {
				lThis.baseField.setValue(null);
				lThis.valueList.push(lVal);
				lThis.setValue(lThis.valueList);
			}
		});
		this.init();
	};
	
	XMultiWrapper.prototype = {
		constructor: XMultiWrapper,
		init: function() {
			this.baseField.init();
		},
		getOptions: function() {
			return this.baseField.getOptions();
		},
		getValue: function() {
			return this.valueList;
		},
		setValue: function(pValue) {
			this.valueList=pValue;
			if (this.$listElement != null) {
				var lHtml = "";
				if (pValue != null) {
					$.each(pValue,function(pIdx,pVal){
						lHtml+='<div class="input-group"><span class="input-group-addon">'+htmlEscape(pVal)
						+'</span><span class="input-group-btn"><button class="btn btn-danger" data-list-index='+pIdx+' type="button">X</button></span></div>';
					});
				}
				this.$listElement.html(lHtml);
				var lThis=this;
				this.$listElement.find('button').on('click',function() {
					var lIdx = $(this).data('list-index');
					lThis.valueList.splice(lIdx,1);
					lThis.setValue(lThis.valueList);
				});
			}
		},
		disable: function() {
			this.baseField.disable();
			this.$container.find('button').prop('disabled',true);
		},
		enable: function() {
			this.baseField.enable();
			this.$container.find('button').prop('disabled',false);
		},
		isDisabled: function() {
			return this.baseField.isDisabled();
		},
		focus: function() {
			this.baseField.focus();
			return true;
		},
		getDisplayValue: function() {
			if (this.valueList==null) return "";
			var lTmpDiv = $("<div>");
			lTmpDiv.text(this.valueList.join(", "));
			return lTmpDiv.html();
		}
	};
}( window.jQuery );

!function( $ ) {
	var XCRUDWrapper = function(element, options) {
		var t=this;
		t.$element = $(element);
		t.defaultTableConfig = {
				paging: true,
				pageLength:20,
				lengthChange: false,
				scrollX: true,
				searching: false,
				select: {style: 'single', info: false, items: 'row'},
				colReorder: {realtime : false}
		};
		t.options = $.extend({},window.defaultXcrudConfig,options,t.$element.data());
		t.data = null;// listing data in standalone mode(resource not specified)
		t.modifyIndex = -1;// standalone mode

		t.init = function() {
			var lCurHrefParts = window.location.href.split('/');
			var lCurHref = lCurHrefParts[lCurHrefParts.length-1];
			var lParent = t.options.parent?t.options.parent+'-':'';
			t.options.page=lCurHref;
			t.options.mainForm$=t.options.mainForm$||t.$element.find('#'+lParent+'frmMain');
			t.options.searchForm$=t.options.searchForm$||t.$element.find('#'+lParent+'frmSearch');
			t.options.searchFormModal$ = t.options.searchForm$.find('div.modal');
			if (t.options.searchFormModal$.length == 0)
				t.options.searchFormModal$ = null;
			t.options.dataTable$=t.options.dataTable$||t.$element.find('#'+lParent+'tblData');
			
			var lTemplateTable$ = t.$element.find('#'+lParent+'divTemplateTableData')
			if (lTemplateTable$.length > 0) {
				var lCompiledTemplate = Handlebars.compile($('#'+lTemplateTable$.data('templateId')).html())
				t.options.templateTable$ = lTemplateTable$.xtemplatetable({template:lCompiledTemplate});
				t.options.templateTable = t.options.templateTable$.data('xtemplatetable')
			}
			t.options.btnSearch$=t.options.btnSearch$||t.options.searchForm$.find('#'+lParent+'btnSearch');
			t.options.btnDownload$=t.options.btnDownload$||t.options.searchForm$.find('#'+lParent+'btnDownloadData');
			t.options.btnDataDownloader$=t.options.btnDataDownloader$||t.options.searchForm$.find('.'+lParent+'data-downloader');
			t.options.btnUpload$=t.options.btnUpload$||t.options.searchForm$.find('#'+lParent+'btnUploadData');
			t.options.btnView$=t.options.btnView$||t.options.searchForm$.find('#'+lParent+'btnView');
			t.options.btnNew$=t.options.btnNew$||t.options.searchForm$.find('#'+lParent+'btnNew');
			t.options.btnModify$=t.options.btnModify$||t.options.searchForm$.find('#'+lParent+'btnModify');
			t.options.btnRemove$=t.options.btnRemove$||t.options.searchForm$.find('#'+lParent+'btnRemove');
			t.options.btnFilter$=t.options.btnFilter$||t.options.searchForm$.find('#'+lParent+'btnFilter');
			t.options.btnFilterClr$=t.options.btnFilterClr$||t.options.searchForm$.find('#'+lParent+'btnFilterClr');
			t.options.btnSave$=t.options.btnSave$||t.options.mainForm$.find('#'+lParent+'btnSave');
			t.options.btnEdit$=t.options.btnEdit$||t.options.mainForm$.find('#'+lParent+'btnEdit');
			t.options.btnClose$=t.options.btnClose$||t.options.mainForm$.find('#'+lParent+'btnClose');
			t.options.btnView$.data('altkey', KEY_VIEW);
			t.options.btnNew$.data('altkey', KEY_NEW);
			t.options.btnModify$.data('altkey', KEY_MODIFY);
			t.options.btnRemove$.data('altkey', KEY_REMOVE);
			
			var lFiltPanel$=t.options.searchForm$.children('fieldset:first-of-type');
			if (lFiltPanel$.hasClass('hidden')) {
				t.options.btnFilter$.html('<span class="fa fa-filter"></span> Show Filter');
				t.options.autoHideFilter = true;
			}

			t.options.allButtons$ = [t.options.btnSearch$, t.options.btnDownload$, t.options.btnDataDownloader$, t.options.btnUpload$, t.options.btnNew$, t.options.btnModify$, 
			                       t.options.btnRemove$, t.options.btnFilter$, t.options.btnFilterClr$, t.options.btnSave$, t.options.btnEdit$, t.options.btnClose$];
			var lPriority = 0;
			$('.btn').each(function() {
				var lTemp = $(this).data('priority');
				if (lTemp)
					lPriority = lPriority>parseInt(lTemp)?lPriority:parseInt(lTemp);
			});
			$.each(t.options.allButtons$, function(pIndex, pValue){
				var lTemp = pValue.data('priority');
				if (!lTemp) {
					lPriority++;
					pValue.data('priority', lPriority);
				}
			});
			t.options.mainForm$.xform(t.options);
			t.options.mainForm = t.options.mainForm$.data('xform');
			t.options.mainFormModal$ = t.options.mainForm$.closest('div.modal');
			if (t.options.mainFormModal$.length == 0)
				t.options.mainFormModal$ = null;
			t.options.forFilter = true;
			t.options.searchForm$.xform(t.options);
			t.options.searchForm = t.options.searchForm$.data('xform');
			t.options.tableConfig = $.extend({},t.defaultTableConfig,window.defaultTableConfig,t.options.tableConfig);
			var lColumns = t.options.tableConfig.columns;
			if (!lColumns) {
				lColumns = [];
				t.options.tableConfig.columns = lColumns;
				if (t.options.dataTable$ && t.options.dataTable$.length > 0) {
					$.each(t.options.dataTable$.find('thead th'), function(pIndex, pValue) {
						lColumns.push($(pValue).data());
					});
				} else if (t.options.templateTable$){
					$.each(t.options.templateTable$.find('thead th'), function(pIndex, pValue) {
						lColumns.push($(pValue).data());
					});
				}
			}
			if (t.options.gridInitHandler)
				t.options.gridInitHandler(t.options.tableConfig.columns);
			var lColNames = [];
			for (var lPtr=0;lPtr<lColumns.length;lPtr++) {
				if (lColumns[lPtr].name) {
					lColNames.push(lColumns[lPtr].name);
					lColumns[lPtr].data=lColumns[lPtr].name;
					lColumns[lPtr].defaultContent="";
					var lConfig = t.getFormConfig(lColumns[lPtr].name);
					if (lConfig) {
						if (!lColumns[lPtr].dataType) lColumns[lPtr].dataType = lConfig.dataType;
						if (!lColumns[lPtr].dataSetValues) lColumns[lPtr].dataSetValues = lConfig.dataSetValues;
						if (!lColumns[lPtr].format) lColumns[lPtr].format = lConfig.format;
					}
				}
				var lClassName = null; 
				switch(lColumns[lPtr].dataType) {
				case STRING:
					lColumns[lPtr].render = $.fn.dataTable.render.text();
					break;
				case INTEGER:
				case DECIMAL:
					if (!lConfig.dataSetValues) lClassName = "dt-right";
					if(lColumns[lPtr].format) {
						var lWrapper = new NumericRenderer(lColumns[lPtr].format);
						lColumns[lPtr].render = lWrapper.renderer;
					}
					break;
				case DATE:
					var lFormat = lColumns[lPtr].format?lColumns[lPtr].format:FORMAT_DATE;
					lColumns[lPtr].parser = new SimpleDateFormat (lFormat);
					var lWrapper = new DateTimeRenderer(lColumns[lPtr].parser);
					lColumns[lPtr].render = lWrapper.renderer;
					lClassName = "dt-center";
					lColumns[lPtr].type = "datetime-fmt-"+lFormat;
					$.fn.dataTable.ext.type.order['datetime-fmt-'+lFormat+'-pre'] = lWrapper.parseTime;
					break;
				case TIME:
					var lFormat = lColumns[lPtr].format?lColumns[lPtr].format:FORMAT_TIME;
					lColumns[lPtr].parser = new SimpleDateFormat(lFormat);
					var lWrapper = new DateTimeRenderer(lColumns[lPtr].parser);
					lColumns[lPtr].render = lWrapper.renderer;
					lClassName = "dt-center";
					lColumns[lPtr].type = "datetime-fmt-"+lFormat;
					$.fn.dataTable.ext.type.order['datetime-fmt-'+lFormat+'-pre'] = lWrapper.parseTime;
					break;
				case DATETIME:
					var lFormat = lColumns[lPtr].format?lColumns[lPtr].format:FORMAT_DATETIME;
					lColumns[lPtr].parser = new SimpleDateFormat(lFormat);
					var lWrapper = new DateTimeRenderer(lColumns[lPtr].parser);
					lColumns[lPtr].render = lWrapper.renderer;
					lClassName = "dt-center";
					lColumns[lPtr].type = "datetime-fmt-"+lFormat;
					$.fn.dataTable.ext.type.order['datetime-fmt-'+lFormat+'-pre'] = lWrapper.parseTime;
					break;
				}
				if (!lColumns[lPtr].className) lColumns[lPtr].className = lClassName;
				if ((lConfig != null) && (lConfig.dataSetValues != null)) {
					var lDataHash = getDatasetHash(lConfig.dataSetValues);
					if (lDataHash != null) {
						var lWrapper = new DatasetRenderer(lDataHash,lColumns[lPtr].render);
						lColumns[lPtr].render = lWrapper.renderer;
					}
				}
			}
//			saveToStorage
			var lSettingsKey = t.options.dataTable$.data("settingsKey");
			if(lSettingsKey == null || lSettingsKey == "")
			{
				lSettingsKey = t.options.page+"";//PageName + TableId
			}
			var lKeyPrefix=(loginData && loginData.login)?(""+loginData.domain+"^"+loginData.login):"^"
			t.defaultTableConfig.storageKey=lKeyPrefix+"^"+lSettingsKey;
			var lTableSelection = t.options.dataTable$.data("selector");
			//if table has attribute of multiselection
			if (lTableSelection != null)
				t.options.tableConfig.select.style="multiple";
			var lSelectStyle = t.options.tableConfig.select.style;
			if (t.options.tableConfig.select.allMethod == null)// none,all,page
				t.options.tableConfig.select.allMethod = "page";
			var lSelectAllMethod = t.options.tableConfig.select.allMethod;
			if (t.options.tableConfig.select.clearSelectionOnPaging == null)
				t.options.tableConfig.select.clearSelectionOnPaging = true;
			if ((lSelectStyle != "single") && (lSelectAllMethod != "none")) {
				// show all checkbox in header
				t.options.selectAll$ = $('<input type="checkbox">');
				t.options.selectAll$.on('click',function(event){
					var lIsChk = $(this).prop('checked');
					$("input[name='chkrowselect']", t.options.dataTable.rows().nodes()).prop('checked', this.checked);
					if(this.checked) {
						if (lSelectAllMethod == "all")
							t.options.dataTable.rows().select();
						else
							t.options.dataTable.rows({ page: 'current' }).select();
					} else { 
						if (lSelectAllMethod == "all")
							t.options.dataTable.rows().deselect();
						else
							t.options.dataTable.rows({ page: 'current' }).deselect();
					}
					event.stopPropagation();
				});
				$("th:first-child",t.options.dataTable$).prepend(t.options.selectAll$);
				t.options.tableConfig.colReorder.fixedColumnsLeft=1;
			}
			t.options.dataTable$.off('page.dt').on( 'page.dt', function (e, settings) {
				if (t.options.tableConfig.select.clearSelectionOnPaging) {
					t.options.dataTable.rows().deselect();
					if (t.options.selectAll$)
						t.options.selectAll$.prop('checked',false);
				} else if (t.options.selectAll$ && (t.options.tableConfig.select.allMethod == 'page')) {
					var lRows = t.options.dataTable.rows({page: 'current'}).nodes();
					var lFlag = true;
					$.each(lRows, function(pIdx,pVal){
						if (!$(pVal).hasClass('selected')) {
							lFlag = false;
							return lFlag;
						}
					});
					t.options.selectAll$.prop('checked',lFlag);
				}
			});
			var lColSelector = t.options.dataTable$.data("colChooser");//col-chooser
			if(lColSelector != null && lColSelector != "")
			{
				//exclude cols list
				var lExcludeList = [];
				for (var lPtr=0;lPtr<lColumns.length;lPtr++) {
					if(!lColumns[lPtr].selExclude)lExcludeList.push(lPtr);
				}
				var lButtonsConf = {
						extend: 'colvis',text:'Choose Columns',//className:'btn btn-primary',
						//columns: ':not(:first-child)',
						container : '#'+lColSelector
				};
				if(lExcludeList.length > 0)
				{
					lButtonsConf.exclude=lExcludeList;
					lButtonsConf.columns=lExcludeList;
				}
				t.options.tableConfig.buttons = [lButtonsConf];
			}
			t.options.tableConfig.columnNames = lColNames;
			t.options.dataTable = t.options.dataTable$.length==0?null:t.options.dataTable$.DataTable(t.options.tableConfig);
			if ((t.options.dataTable != null) && !!!t.options.tableConfig.stateSave)
			{
				//restore state of table then do other activities
				//TODO
				var lState = getFromStorage(t.defaultTableConfig.storageKey);
				if(lState != null)
				{
					applyTableSettings(t.options.dataTable,lState)
				}
				else
				{
					var lDefCols = t.options.defaultColumns;
					if(lDefCols != null){
						lState = {};
						lState.order=lDefCols;
						lState.visible={};
						for(var i=0;i < lDefCols.length;i++)
						{
							lState.visible[lDefCols[i]]=true;
						}
						applyTableSettings(t.options.dataTable,lState)
					}
				}
				t.options.dataTable.on('column-reorder', function ( e, settings, details ) {
					var lCols = settings.aoColumns;
			        var lIdxs = [];
			        //console.log("order : ",lCols);
			        for(var i=0,len=lCols.length;i < len;i++)
			      	{
			        	lIdxs.push(lCols[i].name);
			      	}
			        //console.log("Index : ",lIdxs);
			        //console.log("Mapping : ",details.mapping);
			        var lState = getFromStorage(t.defaultTableConfig.storageKey);
					if(lState == null)
					{
						lState = {};
					}
					lState.order=lIdxs;
					saveToStorage(t.defaultTableConfig.storageKey,lState);
			    });
				t.options.dataTable.on('column-visibility', function ( e, settings, column, state ) {
			        var lCols = settings.aoColumns;
			        var lVisibleMap = {};//Original Column id Indexed
			        //console.log("Visible : ",lCols);
			        for(var i=0,len=lCols.length;i < len;i++)
			      	{
			        	lVisibleMap[lCols[i].name] = lCols[i].bVisible;
			      	}
			        //console.log("Visible : ",lVisibleMap);
			        var lState = getFromStorage(t.defaultTableConfig.storageKey);
					if(lState == null)
					{
						lState = {};
					}
					lState.visible=lVisibleMap;
					saveToStorage(t.defaultTableConfig.storageKey,lState);
			    });
				t.options.dataTable$.attr('tabindex',-1);// allow focus
				t.options.dataTable$.on('click', 'button.form-action-handler, a.form-action-handler', function(pEvent) {
					var lThis$ = $(this);
					var lAction = lThis$.data('action');
			        var lData = t.options.dataTable.row(lThis$.parents('tr')).data();
			        t.formActionHandler(pEvent, lData, lAction);
			    });
			} // end if (t.options.dataTable != null) 
			if (t.options.templateTable) {
				t.options.templateTable$.on('click', 'button.form-action-handler, a.form-action-handler', function(pEvent) {
					var lThis$ = $(this);
					var lAction = lThis$.data('action');
					var lData = lThis$.data(t.options.resource?'selected':'selectedIdx');
					t.formActionHandler(pEvent, lData, lAction);
			    });
			}
			t.options.btnSearch$.off('click').on('click', t.options.searchHandler||t.searchHandler);
			t.options.btnDownload$.off('click').on('click', t.options.downloadHandler||t.downloadHandler);
			t.options.btnDataDownloader$.off('click').on('click', t.options.dataDownloadHandler||t.dataDownloadHandler);
			t.options.btnUpload$.off('click').on('click', t.options.uploadHandler||t.uploadHandler);
			
			if (t.options.mainForm) {
				t.options.btnView$.off('click').on('click', t.options.viewHandler||t.viewHandler);
				t.options.btnNew$.off('click').on('click', t.options.newHandler||t.newHandler);
				t.options.btnModify$.off('click').on('click', t.options.modifyHandler||t.modifyHandler);
				t.options.btnSave$.off('click').on('click', t.options.saveHandler||t.saveHandler);
				t.options.btnEdit$.off('click').on('click', t.options.editHandler||t.editHandler);
				t.options.btnClose$.off('click').on('click', t.options.closeHandler||t.closeHandler);
			}
			t.options.btnFilter$.off('click').on('click', function(pEvent){
				t.showHideFilter();
			});
			t.options.btnFilterClr$.off('click').on('click', function(pEvent){
				if (t.options.searchForm)
					t.options.searchForm.setValue(null);
			});
			t.options.btnRemove$.off('click').on('click', t.options.removeHandler||t.removeHandler);
			if (!t.options.errorHandler) t.options.errorHandler=errorHandler;
			
			// datatable event handlers
			// focus and click first row on page redraw
			t.options.dataTable$.off('draw.dt').on('draw.dt', function() {
				t.options.dataTable$.focus();
		    	//t.options.dataTable.row(':eq(0)', { page: 'current' }).select();//Comment this row to disable auto first row selection
			});
			// arrow handlers
			t.options.dataTable$.off('keydown').on( 'keydown', function (e) {
				var lCode = e.keyCode;
				switch (lCode) {
				case 37:
				case 39:
					var lPageInfo = t.options.dataTable.page.info();
					var lCurPg = lPageInfo.page;
					var lNewPg = lCurPg + (lCode==37?-1:1);
					if (lNewPg < 0) lNewPg = lPageInfo.pages - 1;
					else if (lNewPg >= lPageInfo.pages) lNewPg = 0;
					t.options.dataTable.page(lNewPg).draw('page');
					break;
				case 38:
				case 40:
					var lCurRow = t.getSelectedRow();
					var lIndex = lCurRow.node().rowIndex - 1;
					lIndex += (lCode == 38 ? -1 : 1);
					var lPageInfo = t.options.dataTable.page.info();
					var lSelector;
					if (lIndex < 0) lSelector = ':last';
					else if (lIndex >= (lPageInfo.end - lPageInfo.start)) lSelector = ':first';
					else lSelector = ':eq('+lIndex+')';
					t.options.dataTable.row(lSelector, { page: 'current' }).select();
					break;
				default:
					return;
				}
				e.preventDefault();
			});
			// default filters
			if (t.options.filterStateSave) {
				var lFilter = sessionStorage.getItem("Filters_" + t.defaultTableConfig.storageKey);
				if ((lFilter != null) && (lFilter != ""))
					t.options.searchForm.setValue(JSON.parse(lFilter));
			}
			if (t.options.mainForm && (t.options.new || t.options.modify!=null)) {
				if (t.options.new)
					t.newHandler();
				else
					t.modifyHandler(null, t.options.modify, t.options.viewMode);
			} else if (t.options.searchForm) t.showSearchForm();
			else if (t.options.mainForm) t.newHandler();
		}
		t.showSearchForm = function() {
			if (t.options.searchForm) {
				if (t.options.mainFormModal$) {
					t.options.mainFormModal$.modal('hide');
					t.options.searchForm$.show();
					t.options.dataTable$.focus();
					if (t.options.autoRefresh) {
						if (t.options.searchHandler) t.options.searchHandler();
						else t.searchHandler();
					}
				} else {
					if (t.options.inlineForm) {
						if (t.options.autoRefresh) {
							if (t.options.searchHandler) t.options.searchHandler();
							else t.searchHandler();
						}
					} else {
						t.options.mainForm$.slideUp(100, function() {t.options.searchForm$.slideDown(200, function(){
							t.options.dataTable$.focus();
							if (t.options.autoRefresh) {
								if (t.options.searchHandler) t.options.searchHandler();
								else t.searchHandler();
							}
						});
						});
					}
				}
			}
		}
		t.showMainForm = function(pViewMode) {
			if (t.options.mainForm) {
				if (pViewMode) {
					t.options.btnEdit$.show();
					t.options.btnSave$.hide();
				} else {
					t.options.btnEdit$.hide();
					t.options.btnSave$.show();
				}
				t.options.mainForm.setViewMode(pViewMode);
				if (t.options.mainFormModal$) {
					showModal(t.options.mainFormModal$);
					t.options.mainFormModal$.off('shown.bs.modal').on('shown.bs.modal', function() {t.options.mainForm.focus();});
				} else {
					if (t.options.inlineForm) {
						t.options.mainForm.focus();
					} else {
						t.options.searchForm$.slideUp(100, function() {t.options.mainForm$.slideDown(200, function(){t.options.mainForm.focus();});});
					}
				}
				t.options.mainForm.focus();
			}
		}
		t.getSelectedRow = function() {
			return t.options.dataTable?t.options.dataTable.row({selected:true}):null;
		}
		t.getSelectedRows = function() {
			if(t.options.dataTable) {
				if (t.options.tableConfig.select.style != "single") {
					var lRows = t.options.dataTable.rows({selected:true})[0];
					var lRowObjs=[];
					$.each(lRows, function(pIdx,pVal){
						lRowObjs.push(t.options.dataTable.row(pVal));
					});
					return lRowObjs;
				}
				else
					return t.options.dataTable.row({selected:true});
			}
			else {
				return null;
			}
		}
		t.getSelectedRowsData = function() {
			if (t.options.dataTable) {
				if (t.options.tableConfig.select.style != "single") {
					var lRowsData = [];
					var lRows = t.getSelectedRows();
					if (lRows != null) {
						$.each(lRows, function(pIdx,pVal){
							lRowsData.push(pVal.data());
						});
					}
					return lRowsData;
				} else {
					var lRow=t.getSelectedRow();
					if (lRow) return lRow.data();
				}
			}
			return null;
		}
		t.selectedRowKey = function (pSelected, pSeperator) {
			if ($.isArray(pSelected))
				return pSelected.join('/');
			var lKeyFields = t.options.keyFields;
			if (lKeyFields == null) {
				lKeyFields = [t.options.tableConfig.columns[0].name];
			}
			if (!$.isArray(lKeyFields)) lKeyFields = [lKeyFields];
			var lValues = [];
			for (var lPtr=0;lPtr<lKeyFields.length;lPtr++)
				lValues.push(pSelected[lKeyFields[lPtr]]);
			return lValues.join(pSeperator?pSeperator:'/');
		}
		t.downloadHandler = function(pEvent) {
			if (t.options.resource) {
				var lFilter = t.options.searchForm.getValue(true);
				if (t.options.preSearchHandler && !t.options.preSearchHandler(lFilter))
					return;
				if (t.options.tableConfig.columnNames)
					lFilter.columnNames = t.options.tableConfig.columnNames;
				downloadServerFile(t.options.resource+'/all',t.options.btnDownload$,'POST',JSON.stringify(lFilter));
			}
		}
		t.dataDownloadHandler = function(pEvent) {
			if (t.options.resource) {
				var lFilter = t.options.searchForm.getValue(true);
				if (t.options.preSearchHandler && !t.options.preSearchHandler(lFilter))
					return;
				if (t.options.tableConfig.columnNames)
					lFilter.columnNames = t.options.tableConfig.columnNames;
				downloadServerFile(t.options.resource+'/download/'+$(this).data('type'),t.options.btnDataDownloader$,'POST',JSON.stringify(lFilter));
			}
		}
		t.uploadHandler = function(pEvent) {
			if (t.options.resource) {
				showRemotePage('upload?url='+t.options.resource, null, false);
			}
		}
		t.searchHandler = function(pEvent) {
			if (t.options.searchFormModal$)
				t.options.searchFormModal$.modal('hide');
			if (t.options.resource) {
				var lFilter = t.options.searchForm.getValue(true);
				if (t.options.searchFormModal$) {
					if ($.isEmptyObject(lFilter))
						t.options.btnFilter$.removeClass("btn-warning");
					else
						t.options.btnFilter$.addClass("btn-warning");
				}
				if (t.options.preSearchHandler && !t.options.preSearchHandler(lFilter))
					return;
				if (t.options.tableConfig.columnNames)
					lFilter.columnNames = t.options.tableConfig.columnNames;
				t.options.btnSearch$.prop('disabled',true);
				$.ajax( {
		            url: t.options.resource + '/all',
		            type: 'POST',
		            data:JSON.stringify(lFilter),
		            success: function( pObj, pStatus, pXhr) {
		            	// pre process data
		            	var lRowCount = pObj.length;
		            	var lColumns = t.options.tableConfig.columns;
		            	var lColCount = lColumns.length;
		            	//
		            	var lData = [];
		            	//var lColMap = {};
		            	var lNameIdxMap = {};//{Key : ColumnName, Value : OriginalIndex}
	            		for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++) lData.push({});
		            	if (t.options.dataTable) {
		            		var lCols = t.options.dataTable.settings()[0].aoColumns;
			            	for(var i=0,len=lCols.length;i < len;i++){
			            		//lColMap[lCols[i].name]=lCols[i];
			            		lNameIdxMap[lCols[i].name] = lCols[i]._ColReorder_iOrigCol;
			            		//https://datatables.net/reference/api/colReorder.transpose()
			            		//table.colReorder.transpose( 0, )
			            	}
		            	}
		            	for (var lColPtr=0;lColPtr<lColCount;lColPtr++) {
		            		var lColConfig = lColumns[lColPtr];
		            		var lParser = lColConfig.parser;
		            		var lDataHash = null;//lColConfig.dataSetValues?getDatasetHash(lColConfig.dataSetValues):null;
		            		var lColIdx = t.options.dataTable?lNameIdxMap[lColConfig.name]:lColPtr;
		            		for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++) {
		            			var lRow = pObj[lRowPtr];
		            			if (lColPtr < lRow.length) {
		            				var lValue = lRow[lColPtr];
		            				if (lValue != null) {
		            					if (lParser)
		            						lRow[lColPtr] = lParser.parseDate(lValue);
		            					if (lDataHash) {
		            						lValue = lDataHash[lValue];
		            						if (lValue != null) lRow[lColPtr] = lValue;
		            					}
		            				}
		            			}
		            			if(lColIdx != null){
		            				lData[lRowPtr][lColConfig.name] = htmlEscape(lRow[lColIdx]);
		            			}
		            		}
		            	}
						
						if (t.options.autoHideFilter)
							t.showHideFilter(false);
						else if (t.options.dataTable)
							t.adjustHeight();// adjust datatable height
						if (t.options.filterStateSave) {
							sessionStorage.setItem("Filters_" + t.defaultTableConfig.storageKey, 
									JSON.stringify(t.options.searchForm.getValue(true)));
						}
		            	if (t.options.postSearchHandler && !t.options.postSearchHandler(lColCount==0?pObj:lData)) 
		            		return;
		            	if (t.options.dataTable) {
				          	t.options.dataTable.rows().clear().draw();
				          	//change the positions of data
				            t.options.dataTable.rows.add(lData).draw();
							t.options.dataTable.columns.adjust();
							if (t.options.selectAll$)
								t.options.selectAll$.prop('checked',false);
		            	}
		            	if (t.options.templateTable) {
		            		t.options.templateTable.setData(lData);
		            	}
		            },
		        	error: t.options.errorHandler,
		        	complete: function() {
		        		t.options.btnSearch$.prop('disabled',false);
		        	}
		        });
			} else {
				t.options.dataTable$.resize();
            	if (t.options.postSearchHandler && !t.options.postSearchHandler(t.data)) 
            		return;
	    		var lRowCount = t.data==null?0:t.data.length;
	        	var lColumns = t.options.tableConfig.columns;
	        	var lColCount = lColumns.length;
	        	var lData = [];
	        	for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++) lData.push({});
	        	for (var lColPtr=0;lColPtr<lColCount;lColPtr++) {
	        		var lColConfig = lColumns[lColPtr];
	        		var lParser = lColConfig.parser;
	        		var lDataHash = null;//lColConfig.dataSetValues?getDatasetHash(lColConfig.dataSetValues):null;
	        		for (var lRowPtr=0;lRowPtr<lRowCount;lRowPtr++) {
	        			var lRow = lData[lRowPtr];
	    				var lValue = t.data[lRowPtr][lColConfig.name];
	    				lRow[lColConfig.name] = lValue;
	    				if (lValue != null) {
	    					if (lParser)
	    						lRow[lColConfig.name] = lParser.parseDate(lValue);
	    					if (lDataHash) {
	    						lValue = lDataHash[lValue];
	    						if (lValue != null) lRow[lColConfig.name] = lValue;
	    					}
	    				}
	        		}
	        	}
	        	if (t.options.dataTable) {
	        		t.options.dataTable.rows().clear().draw();
	        		t.options.dataTable.rows.add(lData).draw();
	        	}
            	if (t.options.templateTable) {
            		t.options.templateTable.setData(lData);
            	}

			}
		}
		t.adjustCols=function(pArg) {
			t.options.dataTable.columns.adjust(true).draw();
			//t.options.dataTable.draw()
			//table.columns.adjust().draw();
			//t.options.dataTable.draw();
		}
		t.adjustHeight=function() {
			t.options.dataTable$.resize();
			var lHeight = 0;
			if (t.options.tableConfig.dataTableHeight)
				lHeight = t.options.tableConfig.dataTableHeight(t);
			$.each(t.options.dataTable$.closest('.dataTables_wrapper').children(), function(pIdx,pVal){
				var lChild$ = $(pVal);
				if (lChild$.hasClass('dataTables_scroll'))
					lHeight -= lChild$.children('.dataTables_scrollHead').height();
				else
					lHeight -= lChild$.outerHeight();
			});
			if (lHeight > 0) {
				t.options.dataTable$.closest('.dataTables_scrollBody').css('max-height', lHeight+'px');
				t.options.dataTable.draw();
			}
		}
		t.showHideFilter = function(pShow) {
			if (t.options.searchFormModal$) {
				if ((pShow==null) || pShow) {
					showModal(t.options.searchFormModal$);
					t.options.searchFormModal$.off('shown.bs.modal').on('shown.bs.modal', function() {t.options.searchForm.focus();});
				}
			} else {
				var lFiltPanel$=t.options.searchForm$.children('fieldset:first-of-type');
				if (lFiltPanel$.hasClass('hidden')) {
					if ((pShow==null) || pShow) {
						lFiltPanel$.removeClass('hidden');
						t.options.btnFilter$.html('<span class="fa fa-filter"></span> Hide Filter');
					}
				} else {
					if ((pShow==null) || !pShow) {
						lFiltPanel$.addClass('hidden');
						t.options.btnFilter$.html('<span class="fa fa-filter"></span> Show Filter');
					}
				}
			}
			if (t.options.dataTable)
				t.adjustHeight();
		}
		t.formActionHandler = function(pEvent, pSelected, pAction) {
        	if (t.options.tableConfig.formActionHandler && !t.options.tableConfig.formActionHandler(pEvent, pSelected, pAction))
        		return;
        	if (pAction == 'new')
        		t.newHandler(pEvent);
        	else if (pAction == 'modify')
        		t.modifyHandler(pEvent, pSelected, false);
        	else if (pAction == 'remove')
        		t.removeHandler(pEvent, pSelected);
        	else if (pAction == 'view')
        		t.modifyHandler(pEvent, pSelected, true);
		}
		t.viewHandler = function(pEvent, pSelected) {
			t.modifyHandler(pEvent, pSelected, true);
		}
		t.newHandler = function(pEvent) {
			if (t.options.inlineForm && !moveRow(pEvent, true))
				return;
			if (t.options.preNewHandler && !t.options.preNewHandler(pEvent))
				return;
			t.options.mainForm.setValue(t.options.newDefault, true);
			t.options.mainForm.setMode("insert");
			t.modifyIndex = -1;
	    	if (t.options.postNewHandler && !t.options.postNewHandler(pEvent))
	    		return;
			t.showMainForm(false);
		}
		t.modifyHandler = function(pEvent, pSelected, pViewMode) {
			if (t.options.inlineForm && !moveRow(pEvent, true))
				return;
			var lSelected = pSelected;
			if (lSelected == null) {
				var lSelectedRows = t.getSelectedRowsData();
				if (!!!lSelectedRows || !$.isArray(lSelectedRows))
					lSelected=lSelectedRows;
				else {
					if (lSelectedRows.length>1) {
						alert('Please select only one row to modify');
						return;
					} else if (lSelectedRows.length==1) {
						lSelected = lSelectedRows[0];
					}
				}
			}
			if (lSelected != null) {
				if (t.options.preModifyHandler && !t.options.preModifyHandler(lSelected, pEvent))
					return;
				if (t.options.resource) {
					t.options.btnModify$.prop('disabled',true);
					$.ajax({
			            url: t.options.resource + '/' + t.selectedRowKey(lSelected),
			            type: 'GET',
			            success: function( pObj, pStatus, pXhr) {
			            	t.options.mainForm.setValue(pObj);
			            	t.options.mainForm.setMode("update");
			            	if (t.options.postModifyHandler && !t.options.postModifyHandler(pObj, pEvent))
			            		return;
			    			t.showMainForm(pViewMode);
			            },
			        	error: t.options.errorHandler,
			        	complete: function() {
			        		t.options.btnModify$.prop('disabled',false);
			        	}
			        });
				} else {
					var lSelectedIdx = pSelected!=null?pSelected:t.getSelectedRow().index();
					if (lSelectedIdx >= 0) {
						t.modifyIndex = lSelectedIdx;
			        	t.options.mainForm.setValue(t.data[t.modifyIndex]);
			        	t.options.mainForm.setMode("update");
		            	if (t.options.postModifyHandler)
		            		t.options.postModifyHandler(t.data[t.modifyIndex]);
						t.showMainForm(pViewMode);
					}					
				}
			} else
				alert('Please select a row.');
		}
		t.removeHandler = function(pEvent, pSelected) {
			var lSelected = (pSelected==null)?t.getSelectedRowsData():pSelected;
			if ((lSelected==null)||(lSelected.length==0)) {
				alert('Please select a row.');
			} else {
				var lConfMsg=t.options.removeConfMsg?t.options.removeConfMsg:'You are about to delete the selected record(s). Are you sure?';
				confirm(lConfMsg,'Confirmation','Yes',function(pYes) {
					if (pYes) {
						//var lSelected = t.getSelectedRowsData(); //t.functionToGetRowUsingId(pSelectedIsId);
						
						if (t.options.preRemoveHandler && !t.options.preRemoveHandler(lSelected))
							return;
						if (t.options.resource) {
							t.options.btnRemove$.prop('disabled',true);
							var lIds;
							if ($.isArray(lSelected)) {
								var lRowKeys=[];
								$.each(lSelected,function(pIdx,pVal){
									lRowKeys.push(t.selectedRowKey(pVal,'^'));
								});
								lIds=lRowKeys.join(',');
							} else
								lIds=t.selectedRowKey(lSelected);
							var lResource = t.options.resource;
							var lMethod = 'DELETE';
							if (t.options.noDelete) {
								lResource = lResource + '/delete';
								lMethod = 'POST';
							}
							$.ajax( {
					            url: lResource + '/' + lIds,
					            type: lMethod,
					            success: function( pObj, pStatus, pXhr) {
					            	if (t.options.postRemoveHandler && !t.options.postRemoveHandler(pObj))
					            		return;
					            	alert("Deleted successfully", "Information", function() {
					            		t.showSearchForm();
					            	});
					            },
					        	error: t.options.errorHandler,
					        	complete: function() {
					        		t.options.btnRemove$.prop('disabled',false);
					        	}
					        });					
						} else {
							var lSelectedIdx = pSelected!=null?pSelected:t.getSelectedRow().index();
							var lObj = t.data[lSelectedIdx];
							t.data.splice(lSelectedIdx,1);
			            	if (t.options.postRemoveHandler && !t.options.postRemoveHandler(lObj))
			            		return;
							t.showSearchForm();
						}
					} else 
						t.options.dataTable$.focus();
				});
			}
		}
		t.saveHandler = function(pEvent) {
			if (t.options.preCheckHandler && !t.options.preCheckHandler())
				return false;
			var lErrors = t.options.mainForm.check();
			if ((lErrors != null) && (lErrors.length > 0)) {
				t.showError();
				return false;
			}
				var lData = t.options.mainForm.getValue();
				if (t.options.preSaveHandler && !t.options.preSaveHandler(lData))
					return false;
				var lTmpData = null;
				if (t.options.resource) {
					t.options.btnSave$.prop('disabled',true);
					var lResource = t.options.resource;
					var lMethod = t.options.mainForm.method;
					if ((lMethod == 'PUT') && t.options.noPut) {
						lResource = lResource + '/update';
						lMethod = 'POST';
					}
					$.ajax( {
			            url: lResource,
			            type: lMethod,
			            data:JSON.stringify(lData),
			            success: function( pObj, pStatus, pXhr) {
			            	lTmpData = pObj;
			    			if (t.options.inlineForm && !moveRow(pEvent, false))
			    				return false;
			            	if (t.options.postSaveHandler && !t.options.postSaveHandler(pObj, pEvent))
			            		return true;
			            		var lMsg = "Saved successfully";
			            		if(pObj!=null&&pObj.message!=null&&pObj.message!='')
			            			lMsg = pObj.message;
			            		alert(lMsg, "Information", function() {
			            			if (!t.options.searchForm || (t.options.bulkEntry && t.options.mainForm.method == "POST")) t.newHandler();
			            			else t.showSearchForm();
			            		});
			            },
			        	error: t.options.errorHandler,
			        	complete: function() {
			        		t.options.btnSave$.prop('disabled',false);
			        	}
			        });					
				} else {
					if (t.data == null) t.data = [];
					if (t.options.mainForm.method == "POST")
						t.data.push(lData);
					else if (t.modifyIndex >= 0) {
						t.data.splice(t.modifyIndex, 1, lData);
						t.modifyIndex = -1;
					}					
	            	lTmpData = lData;
	    			if (t.options.inlineForm && !moveRow(pEvent, false))
	    				return false;
	            	if (t.options.postSaveHandler && !t.options.postSaveHandler(lTmpData))
	            		return true;
	            	t.showSearchForm();
				}
			return true;
		}
		t.showError = function() {
				var lResp = appendError(t.options.mainForm.fields, true);
				t.options.focusField = lResp[1];
				alert(lResp[0] , "Validation Failed", function() {
					if (t.options.focusField) t.options.focusField.focus();
				});
			}
		t.editHandler = function(pEvent, pSelected) {
			t.showMainForm(false);
		}
		t.closeHandler = function(pEvent) {
			if (t.options.mainForm.isChanged()) {
				confirm('You will lose changes you have made to the form, if you close it. Are you sure?','Confirmation','Yes',function(pYes) {
					if (pYes) {
						t.options.mainForm.setValue(null);
						t.options.mainForm.setMode(null);
		    			if (t.options.inlineForm && !moveRow(pEvent, false))
		    				return;
						if (t.options.postCloseHandler && !t.options.postCloseHandler(pEvent))
							return;
						t.showSearchForm();
					} else
						t.options.mainForm.focus();
				});
			} else {
				t.options.mainForm.setValue(null);
				t.options.mainForm.setMode(null);
    			if (t.options.inlineForm && !moveRow(pEvent, false))
    				return;
				if (t.options.postCloseHandler && !t.options.postCloseHandler(pEvent))
					return;
				t.showSearchForm();
			}
		}
		t.getFormConfig = function(pName) {
			var lParts = pName.split('.');
			var lConfig = t.options;
			for (var lPtr=0;lPtr<lParts.length;lPtr++) {
				lConfig = getFormConfig(lConfig.fields, lParts[lPtr]);
				if (!lConfig) break;
			}
			return lConfig;
		}
		t.getValue = function() {
			return t.data;
		}
		t.setValue = function(pValue) {
			t.data = pValue;
			t.showSearchForm();
		}
		t.getOptions = function() {
			return t.options;
		}
		t.disable = function() {
			if (t.options.mainForm) t.options.mainForm.disable();
			$.each(t.options.allButtons$, function(pIndex, pValue) {
				pValue.prop('disabled',true);
			});
		}
		t.enable = function() {
			if (t.options.mainForm) t.options.mainForm.enable();
			$.each(t.options.allButtons$, function(pIndex, pValue) {
				pValue.prop('disabled',false);
			});
		}
		t.isDisabled = function() {
			return t.options.mainForm?t.options.mainForm.isDisabled():false;
		}
		t.focus = function() {
			if (t.options.dataTable$.is(':visible')) t.options.dataTable$.focus();
			else t.options.mainForm$.focus();
		}
		t.check = function() {
			return validate(t.data, t.options);
		}
		t.getVisibleColumns = function() {
			var lTable = t.options.dataTable;
			var lCols = lTable.settings()[0].aoColumns;
			var lColNames=[];
			$.each(lCols,function(pIdx,pVal){
				if (pVal.bVisible) lColNames.push(pVal.name);
			});
			return lColNames;
		}
		getFormConfig = function(pFields, pName) {
			var lConfig = null;
			$.each(pFields, function(pIndex, pValue) {
				if (pValue.name==pName) {
					lConfig = pValue;
					return false;
				}
			});
			return lConfig;
		}
		appendError = function(pFields) {
			var lMsg = "", lFirstField = null;
			$.each(pFields, function(pIndex, pValue){
				var lFldObj = pValue.fldObj;
				var lOptions = lFldObj?lFldObj.getOptions():null;
				if (lOptions != null) {
					if (pValue.errors != null) {
					var lLabel = (lOptions&&lOptions.label)?lOptions.label:pValue.name;
					lMsg += "<b>" + lLabel + "</b><ul>";
					$.each(pValue.errors, function(pIndex, pValue){
						lMsg += "<li>" + pValue + "</li>";
					});
					lMsg += "</ul>";
					if (!lFirstField) lFirstField = lFldObj;
					} 
					if ((lOptions.dataType == OBJECT) && (lOptions.fields != null)) {
						var lResp = appendError(lOptions.fields);
						lMsg += lResp[0];
						if (!lFirstField) lFirstField = lResp[1];
					} 
				}
			});	
			return [lMsg, lFirstField];
		}
		moveRow = function(pEvent,pEdit) {
			var lOldEdit = (t.options.mainForm.getMode()!=null);
			var lRow$ = $(pEvent.target).closest("tr");
			if (pEdit) {
				if (lOldEdit) {
					showAlertBox(DANGER,"ERROR : Multiple records cannot be altered simultaneously.",3000);
					return false;
				}
				var lFrmRow$ = t.options.mainForm$.find("tr");
				lFrmRow$.insertBefore(lRow$);
				lRow$.hide();
				lFrmRow$.show();
			} else {
				if (!lOldEdit) {
					return false;
				}
				t.options.mainForm.setValue(null);
				t.options.mainForm.setMode(null);
				lRow$.next().show();
				lRow$.hide();
				t.options.mainForm$.append(lRow$);
				t.options.mainForm.setValue(null);
				t.options.mainForm.setMode(null);
			}
			return true;
		}
		NumericRenderer = function(pFormat) {
			var formatter = new NumberFormatter(pFormat);
			this.renderer = function(pData,pType,pRow) {
				try {
						return formatter.formatNumber(pData);				
				} catch (e) {
						console.log(e);
					return null;
				}
			}
		}
		NumericRenderer.prototype = {constructor: NumericRenderer};
		DateTimeRenderer = function(pFormatter) {
			var formatter = pFormatter;
			this.renderer = function(pData,pType,pRow) {
				try {
					return formatter.formatDate(pData);				
				} catch (e) {
					console.log(e);
					return null;
				}
			};
			this.parseTime = function(pDate) {
				if (pDate != null) {
					var lDate = formatter.parseDate(pDate);
					if (lDate != null)
						return lDate.getTime();
				}
				return 0;
			};
		}
		DateTimeRenderer.prototype = {constructor: DateTimeRenderer};
		DatasetRenderer = function(pDataHash, pOldRenderer) {
			var t=this;
			t.dataHash = pDataHash;
			t.oldRenderer = pOldRenderer;
			t.renderer = function(pData,pType,pRow) {
				var lValue = t.dataHash==null?null:t.dataHash[pData];
				return lValue==null?pData:lValue;
			}
		}
		DatasetRenderer.prototype = {constructor: DatasetRenderer};
		saveToStorage=function(pKey,pObject)
		{
			if (typeof(Storage) !== "undefined") {
				localStorage.setItem(pKey, JSON.stringify(pObject));
			}
		}
		getFromStorage=function(pKey)
		{
			if (typeof(Storage) !== "undefined") {
				var lData = localStorage.getItem(pKey);
				if(lData == null || lData == "")return null;
				return JSON.parse(lData);
			}
			return null;
		}
		applyTableSettings=function(pTable,pObject)
		{
			var lColMap = {};
			var lCols = pTable.settings()[0].aoColumns;
			for(var i=0,len=lCols.length;i < len;i++)
			{
				lColMap[lCols[i].name]=lCols[i];
			}
			//console.log(pTable.columns().indexes());
			if(pObject.order != null && pObject.order.length > 0)
			{
				var lNewOrd = [];
				for(var i=0,len=pObject.order.length;i < len;i++)
				{
					var lColObj = lColMap[pObject.order[i]];
					if(lColObj == null)continue;
					lNewOrd.push(lColObj._ColReorder_iOrigCol);
				}
				//console.log(lNewOrd);
				for(var i=0,len=lCols.length;i < len;i++)
				{
					var lColObj = lColMap[lCols[i].name];
					if(lColObj == null)continue;
					var lOIdx = lColObj._ColReorder_iOrigCol;
					if($.inArray(lOIdx,lNewOrd) < 0){
						lNewOrd.push(lColObj._ColReorder_iOrigCol);
					}
				}
				//console.log(lNewOrd);
				pTable.colReorder.order(lNewOrd,false);
			}
				
			if(pObject.visible != null)
			{
				for(var i=0,len=lCols.length;i < len;i++)
				{
					var lColObj = lColMap[lCols[i].name];
					if(lColObj == null)continue;
					var lOIdx = lColObj._ColReorder_iOrigCol;
					var lNewIdx = lColObj.idx;
					var lVisible = pObject.visible[lCols[i].name];//pObject.visible[lOIdx];
					if(lVisible == null)lVisible = false;
					//console.log(">>",lColObj.idx,lCols[i].name,pObject.visible[lCols[i].name])
					pTable.column(lNewIdx).visible(lVisible);
				}
			}
		}
		
		t.init();
	};

	
	$.fn.xcrudwrapper = function(option, val) {
		return this.each(function() {
			var $this = $(this),
			data = $this.data('xcrudwrapper'),
			options = typeof option === 'object' && option;
			if (!data) {
				data = new XCRUDWrapper(this, $.extend(true,{},$.fn.xcrudwrapper.defaults,options));
				$this.data('xcrudwrapper', data);
			}
			if (typeof option === 'string') data[option](val);
		});
	};
}( window.jQuery );

$(window).off('keydown').on( 'keydown', function(e){
	var lCode = e.keyCode;
	var lModal = null;
	var lZIndex = 0;
	$('.modal:visible').each(function(){
		var lTempModal$ = $(this);
		var lTemp = parseInt(lTempModal$.css('z-index'));
		if (lZIndex < lTemp) {
			lZIndex = lTemp;
			lModal = lTempModal$;
		}
	});
	var lBtn$ = null;
	if (e.altKey)
		lBtn$ = getButton(".btn-default", lCode);
	else if (lModal) {
		if (lCode == KEY_ENTER) {
			lBtn$ = lModal.find('.btn-primary:visible');
			if (lBtn$.length == 0)
				lBtn$ = lModal.find('.btn-enter:visible');
		}
	} else {
		if (lCode == KEY_ENTER) {
			lBtn$ = getButton(".btn-primary", null);
			if (lBtn$==null)
				lBtn$ = getButton(".btn-enter", null);
		} else if (lCode == KEY_ESCAPE) {
			lBtn$ = getButton(".btn-close", null);
			// todo: if not esc button found then datatable toggle focus 
		}
	}
	if (lBtn$ && (lBtn$.length==1)) {
		if (!lBtn$.prop('disabled'))
			lBtn$.trigger('click');
		e.preventDefault();
	}
});
function compareJson(pJson1, pJson2) {
	if (!pJson1 && !pJson2) return true;
	var lJson1=pJson1?pJson1:pJson2;
	var lJson2=pJson1?pJson2:pJson1;
	var lEquals = true;
	if ($.isPlainObject(lJson1)) {
		if (!lJson2) lJson2 = {};
		if (!$.isPlainObject(lJson2)) return false;
		var lAllKeys = $.extend({},lJson1, lJson2);
		$.each(lAllKeys, function(pIndex, pValue) {
			lEquals = compareJson(lJson1[pIndex], lJson2[pIndex]);
			return lEquals;
		});
	} else if ($.isArray(lJson1)) {
		if (!lJson2) lJson2 = [];
		if (!$.isArray(lJson2)) return false;
		if (lJson1.length != lJson2.length) return false;
		$.each(lJson1, function (pIndex) {
			lEquals = compareJson(lJson1[pIndex], lJson2[pIndex]);
			return lEquals;
		});
	} else
		lEquals = lJson1==lJson2;
	return lEquals;
}
function getButton(pSelector, pAltKey) {
	var lBtns = null;
	var lMaxPriority = -1;
	$(pSelector + ':visible').each(function(){
		var lThis$ = $(this);
		var lThisPriority = lThis$.data('priority');
		var lThisIntPriority = lThisPriority?parseInt(lThisPriority):0;
		var lThisAltKey = lThis$.data('altkey');
		if ((!pAltKey && !lThisAltKey) || (pAltKey === lThisAltKey)) {
			if (lThisIntPriority > lMaxPriority) {
				lBtns = [lThis$];
				lMaxPriority = lThisIntPriority;
			} else if (lThisIntPriority == lMaxPriority) {
				if (lBtns) lBtns.push(lThis$);
				else lBtns = [lThis$];
			}
		}
	});
	return (lBtns&&(lBtns.length==1))?lBtns[0]:null;
}
function validate(pVal, pOptions, pDraftMode) {
	var lErrors = [];
	if (pOptions.notNull && !pDraftMode) {
		if ((pVal==null)||(pVal==''&&!pOptions.allowBlank))
			lErrors.push(MESSAGE_NOTNULL);
	}
	if (pOptions.allowMultiple) {
		if (pVal && !$.isArray(pVal)) {
			pVal = [pVal];
			//lErrors.push(MESSAGE_SHOULDBELIST);
			//return lErrors;
		}
		var lItemCount = pVal?pVal.length:0;
		if (pOptions.minItems && (lItemCount < pOptions.minItems) && !pDraftMode)
			lErrors.push(MESSAGE_MINITEMS + pOptions.minItems);
		if (pOptions.maxItems && (lItemCount > pOptions.maxItems))
			lErrors.push(MESSAGE_MAXITEMS + pOptions.maxItems);
		for (var lItemPtr=0;lItemPtr<lItemCount;lItemPtr++)
			validateValue(pVal[lItemPtr], pOptions, lErrors);
	} else {
		if (pVal)
			validateValue(pVal, pOptions, lErrors);
	}
	return lErrors;
}
function validateValue(pVal, pOptions, pErrors) {
	var lErrors = pErrors || [];
	switch (pOptions.dataType) {
	case STRING :
		if(pVal!=null) pVal = pVal.trim(); //for pattern matching errors when trailing or pefix of spaces
		if (pOptions.minLength && pOptions.maxLength && (pOptions.maxLength == pOptions.minLength) && (pVal.length != pOptions.minLength))
			lErrors.push(MESSAGE_EQUALLENGTH + pOptions.minLength);
		else {
		if (pOptions.minLength && (pVal.length < pOptions.minLength))
			lErrors.push(MESSAGE_MINLENGTH + pOptions.minLength);
		if (pOptions.maxLength && (pVal.length > pOptions.maxLength))
			lErrors.push(MESSAGE_MAXLENGTH + pOptions.maxLength);
		}
		if (pOptions.pattern) {
			var lPattern = PATTERNS[pOptions.pattern];
			if (!lPattern) lPattern = pOptions.pattern;
			if (!(new RegExp(lPattern,"g")).test(pVal)) {
				var lPatternMessage = pOptions.patternMessage;
				lErrors.push(MESSAGE_PATTERN + (lPatternMessage?lPatternMessage:""));
			}
		}
		break;
	case INTEGER :
		if (!(new RegExp("^[+-]?\\d+$","g")).test(pVal)) {
			lErrors.push(MESSAGE_SHOULDBEINTEGER);
			return lErrors;
		}
		var lIntVal = parseInt(pVal);
		if ((pOptions.minValue!=null) && (lIntVal < pOptions.minValue))
			lErrors.push(MESSAGE_MINVALUE + pOptions.minValue);
		if ((pOptions.maxValue!=null) && (lIntVal > pOptions.maxValue))
			lErrors.push(MESSAGE_MAXVALUE + pOptions.maxValue);
		break;
	case DECIMAL : 
		if (!(new RegExp("^[+-]?\\d+(\\.\\d+)?$","g")).test(pVal)) {
			lErrors.push(MESSAGE_SHOULDBEDECIMAL);
			return lErrors;
		}
		var lDecVal = parseFloat(pVal);
		if ((pOptions.minValue!=null) && (lDecVal < pOptions.minValue))
			lErrors.push(MESSAGE_MINVALUE + pOptions.minValue);
		if ((pOptions.maxValue!=null) && (lDecVal > pOptions.maxValue))
			lErrors.push(MESSAGE_MAXVALUE + pOptions.maxValue);
		var lParts = pVal.split('.');
		if ((pOptions.integerLength!=null) && (pOptions.integerLength!=false) && lParts[0].length > pOptions.integerLength)
			lErrors.push(MESSAGE_INTEGERLENGTH + pOptions.integerLength);
		if ((pOptions.decimalLength!=null) && (pOptions.decimalLength!=false) && lParts.length > 1 && lParts[1].length > pOptions.decimalLength)
			lErrors.push(MESSAGE_DECIMALLENGTH + pOptions.decimalLength);
		break;
	case DATE :
	case TIME :
	case DATETIME :
		var lFormatter = new SimpleDateFormat(pOptions.format);
		var lDateVal = lFormatter.parseDate(pVal);
		if (pOptions.minValue && (lDateVal < lFormatter.parseDate(pOptions.minValue)))
			lErrors.push(MESSAGE_MINVALUE + pOptions.minValue);
		if (pOptions.maxValue && (lDateVal > lFormatter.parseDate(pOptions.maxValue)))
			lErrors.push(MESSAGE_MAXVALUE + pOptions.maxValue);
		break;
	}
	return lErrors;
}
function applyConversions(pVal, pConversions) {
	var lVal = pVal;
	if (lVal) {
		for (var lPtr in pConversions) {
			switch (pConversions[lPtr]) {
			case 'toUpper' : 
				lVal = lVal.toUpperCase();
				break;
			case 'toLower' : 
				lVal = lVal.toLowerCase();
				break;
			}
			lVal = lVal.toUpperCase();
		}
	}
	return lVal;
}
function clearDataset(pDataSet) {
	dataSetCache[pDataSet] = null;
}
function getDataset(pDataSet) {
	if (typeof pDataSet === 'object') 
		return pDataSet;
	else {
		var lDataSet = getDatasetFromCache(pDataSet);
		return lDataSet==null?null:lDataSet[0];
	}
}
function getDatasetFromCache(pDataSet) {
	var lDataSet = dataSetCache[pDataSet];
	if (lDataSet == null) {
		$.ajax( {
            url: pDataSet,
            type: 'GET',
            success: function( pObj, pStatus, pXhr) {
            	lDataSet = [pObj, null];
            	dataSetCache[pDataSet] = lDataSet;
            },
        	error: errorHandler,
        	async: false
        });	
	}
	return lDataSet;
}
function getDisplayValue(pFieldObj) {
	var lVal = pFieldObj.getValue();
	if (lVal == null) return lVal;
	var lShowCode=null;
	if (pFieldObj.getOptions().dataAttributes != null) lShowCode=pFieldObj.getOptions().dataAttributes['dispMode'];
	if (pFieldObj.getOptions().dataSetValues) {
		var lDataSetHash = getDatasetHash(pFieldObj.getOptions().dataSetValues);
		var lDispVal;
		if ($.isArray(lVal)) {
			var lDispVal = [];
			lDispVal.push('<ul>');
			var lTmpDiv = $("<div>");
			$.each(lVal, function(pIndex, pValue){
				var lRevVal = lDataSetHash[pValue];
				lRevVal = buildDisplayValue(pValue,lRevVal,null,lShowCode);
				//lDispVal.push('<li>' + (lRevVal?lRevVal:'['+pValue+']') + '</li>');
				lTmpDiv.text(lRevVal?lRevVal:'['+pValue+']');
				//'<img src="a" onerror="alert(\'haxxored\');" />'
				lDispVal.push('<li>' + (lTmpDiv.html()) + '</li>');
			});
			lDispVal.push('</ul>');
			return lDispVal.join('');
		} else {
			var lRevVal = lDataSetHash[lVal];
			lRevVal = buildDisplayValue(lVal,lRevVal,null,lShowCode);
			//return lRevVal?lRevVal:'['+lVal+']';
			var lTmpDiv = $("<div>");
			lTmpDiv.text(lRevVal?lRevVal:'['+lVal+']');
			return lTmpDiv.html();
		}
	} else {
		//return lVal;
		var lTmpDiv = $("<div>");
		lTmpDiv.text(lVal);
		return lTmpDiv.html();
	}
		
}
function getDatasetHash(pDataSet) {
	var lDataHash = {};
	var lDataSet = null;
	if (typeof pDataSet === 'object') lDataSet = pDataSet;
	else {
		var lDataSetFromCache = getDatasetFromCache(pDataSet);
		if (lDataSetFromCache != null) {
			lDataSet = lDataSetFromCache[0];
			if (lDataSetFromCache[1] == null) lDataSetFromCache[1] = lDataHash;
			else lDataHash = lDataSetFromCache[1];
		}
	}
	if ((lDataHash == null || $.isEmptyObject(lDataHash)) && (lDataSet != null)) {
		$.each(lDataSet, function(pIndex, pValue) {
			if ($.isArray(pValue.children)) {
				$.each(pValue.children, function(index,subOption) {
					if (typeof subOption === 'object')
						lDataHash[subOption.value] = (subOption.text || subOption.value);
					else lDataHash[subOption] = subOption;
				});
			}
			else {
				if (typeof pValue === 'object')
					lDataHash[pValue.value] = (pValue.text || pValue.value);
				else lDataHash[pValue] = pValue;
			}
		});
	}
	return lDataHash;
}
function populateOptions(pSelect$, pDataSet, pStaticLength) {
    if (!pDataSet) return;
    if (pStaticLength==null) pStaticLength=0;
    pSelect$.find('optgroup').remove();
    pSelect$.find('option').slice(pStaticLength).remove();
	var optionDOM = "";
    var groupCounter = 0;
    var lShowCode=pSelect$.data('dispMode');
    var lDataSetValues = getDataset(pDataSet);
		$.each(lDataSetValues, function(pIndex, pValue) {
		if (typeof pValue === 'object') {
	        if ($.isArray(pValue.children)) {
	            groupCounter++;
	            optionDOM += '<optgroup label="' + (pValue.title || 'Group ' + groupCounter) + '">';
	
	            $.each(pValue.children, function(index,subOption) {
	            	/*var lDispText = (subOption.text || subOption.value);
	            	if (lShowCode=='prefix') lDispText = subOption.value + ' - ' + lDispText;
	            	else if (lShowCode=='suffix') lDispText = lDispText + ' - ' + subOption.value;
	            	else if (lShowCode=='onlycode') lDispText = subOption.value;
	            	else if (subOption.desc) lDispText += ' - ' + subOption.desc;*/
	            	var lDispText = buildDisplayValue(subOption.value, subOption.text, subOption.desc, lShowCode);
	                optionDOM += '<option value="' + subOption.value + '">' + lDispText + '</option>';
	            });
	
	            optionDOM += '</optgroup>';
	        }
	        else {
	        	/*var lDispText = (pValue.text || pValue.value);
            	if (lShowCode=='prefix') lDispText = pValue.value + ' - ' + lDispText;
            	else if (lShowCode=='suffix') lDispText = lDispText + ' - ' + pValue.value;
            	else if (lShowCode=='onlycode') lDispText = pValue.value;
            	else if (pValue.desc) lDispText += ' - ' + pValue.desc;*/
            	var lDispText = buildDisplayValue(pValue.value, pValue.text, pValue.desc, lShowCode);
	            optionDOM += '<option value="' + pValue.value + '">' + lDispText + '</option>';
	        }
	    } else {
	    	optionDOM += '<option value="' + pValue + '">' + pValue + '</option>';
	    }
    });
    
	pSelect$.append(optionDOM);
}
function buildDisplayValue(pValue,pText,pDesc,pDispMode) {
	var lDispText = (pText || pValue);
	if (pDispMode=='prefix') lDispText = pValue + ' - ' + lDispText;
	else if (pDispMode=='suffix') lDispText = lDispText + ' - ' + pValue;
	else if (pDispMode=='onlycode') lDispText = pValue;
	else if (pDesc) lDispText += ' - ' + pDesc;
	return lDispText;
}
function isNullOrEmpty(pValue) {
	return (pValue==null)||(pValue=='');
}
/** Date Formatter **/
var dateFormatComponents = {
    dd: {property: 'UTCDate', getPattern: function() { return '(0?[1-9]|[1-2][0-9]|3[0-1])\\b';}, dateLevel: 0},
    MM: {property: 'UTCMonth', getPattern: function() {return '(0?[1-9]|1[0-2])\\b';}, dateLevel: 1},
    yy: {property: 'UTCYear', getPattern: function() {return '(\\d{2})\\b'}, dateLevel: 2},
    yyyy: {property: 'UTCFullYear', getPattern: function() {return '(\\d{4})\\b';}, dateLevel: 2},
    HH: {property: 'UTCHours', getPattern: function() {return '(0?[0-9]|1[0-9]|2[0-3])\\b';}},
    mm: {property: 'UTCMinutes', getPattern: function() {return '(0?[0-9]|[1-5][0-9])\\b';}},
    ss: {property: 'UTCSeconds', getPattern: function() {return '(0?[0-9]|[1-5][0-9])\\b';}, seconds:true},
    SS: {property: 'UTCMilliseconds', getPattern: function() {return '([0-9]{1,3})\\b';}},
    hh: {property: 'Hours12', getPattern: function() {return '(0?[1-9]|1[0-2])\\b';}, hour12:true},
    aa: {property: 'Period12', getPattern: function() {return '(AM|PM|am|pm|Am|aM|Pm|pM)\\b';}, hour12:true},
    MMM: {property: 'Month', getPattern: function() {return '(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\b';}, dateLevel: 1}
};
var months=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
var keys = [];
for (var k in dateFormatComponents) keys.push(k);
keys[keys.length - 1] += '\\b';
keys.push('.');

var formatComponent = new RegExp(keys.join('\\b|'));
keys.pop();
var formatReplacer = new RegExp(keys.join('\\b|'), 'g');
function escapeRegExp(str) {
	    // http://stackoverflow.com/questions/3446170/escape-string-for-use-in-javascript-regex
    return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}
function UTCDate() {
	return new Date(Date.UTC.apply(Date, arguments));
}
function padLeft(s, l, c) {
    if (l < s.length) return s;
    else return Array(l - s.length + 1).join(c || ' ') + s;
}

var NumberFormatter = function (pFormat) {
	this.format = pFormat;
	this.zeroString="0000000000000000000000000"
	this._compileFormat();
}
NumberFormatter.prototype = {
    constructor: NumberFormatter,
    _compileFormat: function () {
    	this.decSep='.';
    	this.isDecimal=this.format.indexOf(this.decSep) >= 0;
    	this.grpSep=null;
    	//search for separator for grp & decimal, anything not digit, not +/- sign, not #.
    	var lSeperators = this.format.match(/[^\d\-\+#]/g);
    	if(this.isDecimal) {
    		this.grpSep = (lSeperators && lSeperators[1] && lSeperators[0]);
    	} else {
    		this.grpSep = (lSeperators && lSeperators[lSeperators.length-1]);
    	}
    	this.hasSeperator = this.grpSep != null && this.format.indexOf(this.grpSep) >= 0;
    	//variable 
    	this.fmtSplit=(this.isDecimal)?this.format.split(this.decSep):[this.format];
    	this.revFmtArray=this.fmtSplit[0].split('').reverse();
    },
    formatNumber : function(pValue){
        if (!this.format || pValue == null || pValue == "" || isNaN(+pValue)) {
            return pValue; //return as it is.
        }
        if(typeof pValue === 'string')pValue = +pValue;
        var isNegative = pValue<0? pValue= -pValue: 0; //process only abs(), and turn on flag.
    	var lPart = [];
    	if(this.isDecimal) {
    		//Fix the decimal first, toFixed will auto fill trailing zero.
    		pValue = pValue.toFixed( this.fmtSplit[1] && this.fmtSplit[1].length);
    		pValue = +(pValue) + ''; //convert number to string to trim off *all* trailing decimal zero(es)
    		//fill back any trailing zero according to format
    		var lTailZeroPos = this.fmtSplit[1] && this.fmtSplit[1].lastIndexOf('0'); //look for last zero in format
    		lPart = pValue.split('.');
    		if (!lPart[1] || lPart[1] && lPart[1].length <= lTailZeroPos) {
    			var lDecPart = !lPart[1]?"":lPart[1];
    			//we must add the n tailing zeros
    			if(lTailZeroPos != "")
    				lPart[1] = lDecPart + this.zeroString.substring(0,lTailZeroPos+1-lDecPart.length);
    		}
    	} else {
    		pValue = pValue + '';
    		pValue = pValue.split(this.decSep)[0];
    		lPart[0]=pValue;
    	}
    	//add leading zeros if format specifies it
    	var lIntPrt = lPart[0];
    	var lFormatTmp = (this.hasSeperator)?this.fmtSplit[0].split(this.grpSep).join(''):this.fmtSplit[0]; //join back without separator for counting the pos of any leading 0.
    	var lLeadZeroPos = lFormatTmp && lFormatTmp.indexOf('0');
    	if (lLeadZeroPos > -1 ) {
    		while (lIntPrt.length < (lFormatTmp.length - lLeadZeroPos)) {
    			lIntPrt = '0' + lIntPrt;
    		}
    	}
    	else if (+lIntPrt == 0){
    		lIntPrt = '';
    	}
    	lPart[0]=lIntPrt;
    	//Leading zeros ends
    	if(this.hasSeperator) {
    		var lNum = [];
    		var lRevNo = lPart[0].split('').reverse();
    		var lActLen = 0;
    		for (var i = 0; i < lRevNo.length; i++) {
    			if(this.revFmtArray[lActLen++] == this.grpSep){
    				lNum.push(this.grpSep);
    				lActLen++;
    			}
    			lNum.push(lRevNo[i]);
    		}
    		lPart[0]=lNum.reverse().join('');
    	}
    	return (isNegative?'-':'') + lPart[0] + (this.isDecimal && (lPart[1] && lPart[1].length > 0)?this.decSep+lPart[1] : "") ; //put back any negation and combine integer and fraction.
    }
}

var SimpleDateFormat = function (pFormat) {
	this.format = pFormat;
	this._compileFormat();
}
SimpleDateFormat.prototype = {
    constructor: SimpleDateFormat,
    parseDate: function(str) {
    	if (str==null) return null;
        var match, i, property, methodName, value, parsed = {};
        str = this._evaluateDateKeyword(str);
        if (!(match = this._formatPattern.exec(str)))
          return null;
        for (i = 1; i < match.length; i++) {
          property = this._propertiesByIndex[i];
          if (!property)
            continue;
          value = match[i];
          if (/^\d+$/.test(value))
            value = parseInt(value, 10);
          parsed[property] = value;
        }
        return this._finishParsingDate(parsed);
      },
      formatDate: function(d) {
    	  if (d==null) return "";
          return this.format.replace(formatReplacer, function(match) {
            var methodName, property, rv, len = match.length;
            if (match === 'ms')
              len = 1;
            property = dateFormatComponents[match].property
            if (property === 'Hours12') {
              rv = d.getUTCHours();
              if (rv === 0) rv = 12;
              else if (rv !== 12) rv = rv % 12;
            } else if (property === 'Period12') {
              if (d.getUTCHours() >= 12) return 'PM';
              else return 'AM';
            } else if (property === 'UTCYear') {
              rv = d.getUTCFullYear();
              rv = rv.toString().substr(2); 
            } else if (property === 'Month') {
              rv = months[d.getUTCMonth()];
            } else {
              methodName = 'get' + property;
              rv = d[methodName]();
            }
            if (methodName === 'getUTCMonth') rv = rv + 1;
            return padLeft(rv.toString(), len, '0');
          });
        },
    test: function(pVal) {
    	return this._formatPattern.test(pVal)
    },
    _evaluateDateKeyword: function(str) {
    	var lDate = new Date();
    	var lRetDate = null;
    	if (KEYWORD_CURRENTDATE === str)
    		str = this.formatDate(new Date(Date.UTC(lDate.getFullYear(), lDate.getMonth(), lDate.getDate(), 0, 0, 0, 0)));
        else if (KEYWORD_CURRENTTIME === str)
        	str = this.formatDate(new Date(Date.UTC(0, 0, 0, lDate.getHours(), lDate.getMinutes(), lDate.getSeconds(), lDate.getMilliseconds())));
        else if (KEYWORD_CURRENTDATETIME === str)
        	str = this.formatDate(new Date(Date.UTC(lDate.getFullYear(), lDate.getMonth(), lDate.getDate(), lDate.getHours(), lDate.getMinutes(), lDate.getSeconds(), lDate.getMilliseconds())));
        return str;
    },    
    _finishParsingDate: function(parsed) {
        var year, month, date, hours, minutes, seconds, milliseconds;
        year = parsed.UTCFullYear;
        if (parsed.UTCYear) year = 2000 + parsed.UTCYear;
        if (!year) year = 1970;
        if (parsed.UTCMonth) month = parsed.UTCMonth - 1;
        else if (parsed.Month) month = $.inArray(parsed.Month,months);
        else month = 0;
        date = parsed.UTCDate || 1;
        hours = parsed.UTCHours || 0;
        minutes = parsed.UTCMinutes || 0;
        seconds = parsed.UTCSeconds || 0;
        milliseconds = parsed.UTCMilliseconds || 0;
        if (parsed.Hours12) {
          hours = parsed.Hours12;
        }
        if (parsed.Period12) {
          if (/pm/i.test(parsed.Period12)) {
            if (hours != 12) hours = (hours + 12) % 24;
          } else {
            hours = hours % 12;
          }
        }
        return UTCDate(year, month, date, hours, minutes, seconds, milliseconds);
      },
    _compileFormat: function () {
        var match, component, components = [], 
        str = this.format, propertiesByIndex = {}, i = 0, pos = 0,
        dateLevel = 99, hour12 = false, seconds = false;
        while (match = formatComponent.exec(str)) {
          component = match[0];
          if (component in dateFormatComponents) {
            i++;
            propertiesByIndex[i] = dateFormatComponents[component].property;
            components.push('\\s*' + dateFormatComponents[component].getPattern(
              this) + '\\s*');
            hour12 = dateFormatComponents[component].hour12||hour12;
            seconds = dateFormatComponents[component].seconds||seconds;
            if (dateFormatComponents[component].dateLevel != null)
            	dateLevel = dateLevel<dateFormatComponents[component].dateLevel?dateLevel:dateFormatComponents[component].dateLevel;
          }
          else {
            components.push(escapeRegExp(component));
          }
          str = str.slice(component.length);
        }
        this.hour12 = hour12;
        this.seconds = seconds;
        this.dateLevel = dateLevel==99?0:dateLevel;
        this._formatPattern = new RegExp(
          '^\\s*' + components.join('') + '\\s*$');
        this._propertiesByIndex = propertiesByIndex;
      },    
}

/** window alert and confirm boxes **/
window.oldAlert = window.alert;
window.oldConfirm = window.confirm;
window.oldPrompt = window.prompt;
window.alert = function(message, title, callback) {
    if($("#bootstrap-alert-box-modal").length == 0) {
        $("body").append('<div id="bootstrap-alert-box-modal" class="modal fade" tabindex=-1>\
            <div class="modal-dialog">\
                <div class="modal-content">\
                    <div class="modal-header" style="min-height:40px;">\
                        <div class="modal-title"><span></span>\
        					<div class="btn-group pull-right">\
        					<button class="btn btn-default btn-sm" onClick="javascript:toggleModalSize(this)" title="Expand/Shrink"><i class="fa fa-expand"></i></button>\
        					<button class="btn btn-default btn-sm" data-dismiss="modal" title="Close"><i class="fa fa-remove"></i></button>\
							</div>\
						</div>\
                    </div>\
                    <div class="modal-body"><p></p></div>\
                    <div class="modal-footer">\
                        <button type="button" data-dismiss="modal" class="btn btn-primary">Ok</button>\
                    </div>\
                </div>\
            </div>\
        </div>');
        enableModalDragging($("#bootstrap-alert-box-modal .modal-header"));
    }
    $("#bootstrap-alert-box-modal").off('hidden.bs.modal').on('hidden.bs.modal', function () {
        if(callback) callback();
    });
    $("#bootstrap-alert-box-modal .modal-header .modal-title span").html(title || "Alert!");
    $("#bootstrap-alert-box-modal .modal-body p").empty().append(message || "");
    var lAlertBox = $("#bootstrap-alert-box-modal");
    try {
    	lAlertBox.modal({keyboard:true});
    } catch (e) {oldAlert(e)}
    showModal(lAlertBox);
};
window.confirm = function(message, title, yes_label, callback) {
    $("#bootstrap-confirm-box-modal").data('confirm-yes', false);
    if($("#bootstrap-confirm-box-modal").length == 0) {
        $("body").append('<div id="bootstrap-confirm-box-modal" class="modal fade" tabindex=-1>\
            <div class="modal-dialog">\
                <div class="modal-content">\
                    <div class="modal-header" style="min-height:40px;">\
                        <span class="modal-title"></span>\
						<div class="btn-group pull-right">\
						<button class="btn btn-default btn-sm" onClick="javascript:toggleModalSize(this)" title="Expand/Shrink"><i class="fa fa-expand"></i></button>\
						<button class="btn btn-default btn-sm" data-dismiss="modal" title="Close"><i class="fa fa-remove"></i></button>\
						</div>\
                    </div>\
                    <div class="modal-body"><p></p></div>\
                    <div class="modal-footer">\
        				<div class="btn-group pull-right">\
                		<button type="button" class="btn btn-primary">Ok</button>\
                        <button type="button" data-dismiss="modal" class="btn btn-default">No</button>\
        				</div>\
                    </div>\
                </div>\
            </div>\
        </div>');
        $("#bootstrap-confirm-box-modal .modal-footer .btn-primary").off('click').on('click', function () {
            $("#bootstrap-confirm-box-modal").data('confirm-yes', true);
            $("#bootstrap-confirm-box-modal").modal('hide');
            return false;
        });
        enableModalDragging($("#bootstrap-confirm-box-modal .modal-header"));
    }
    $("#bootstrap-confirm-box-modal").off('hidden.bs.modal').on('hidden.bs.modal', function () {
        if(callback) callback($("#bootstrap-confirm-box-modal").data('confirm-yes'));
    });
 
    $("#bootstrap-confirm-box-modal .modal-header span").html(title || "Confirm?");
    $("#bootstrap-confirm-box-modal .modal-body p").html(message || "");
    $("#bootstrap-confirm-box-modal .modal-footer .btn-primary").text(yes_label || 'Ok');
    var lConfirmBox = $("#bootstrap-confirm-box-modal");
    lConfirmBox.modal();
    showModal(lConfirmBox);
};
window.prompt = function(message, title, callback, cancelCallback) {
	$("#bootstrap-prompt-box-modal").data('prompt-ok', false);
	$("#bootstrap-prompt-box-modal #text-prompt").val('');
    if($("#bootstrap-prompt-box-modal").length == 0) {
        $("body").append('<div id="bootstrap-prompt-box-modal" class="modal fade" tabindex=-1>\
            <div class="modal-dialog">\
                <div class="modal-content">\
                    <div class="modal-header" style="min-height:40px;">\
                        <span class="modal-title"></span>\
						<div class="btn-group pull-right">\
						<button class="btn btn-default btn-sm" onClick="javascript:toggleModalSize(this)" title="Expand/Shrink"><i class="fa fa-expand"></i></button>\
						<button class="btn btn-default btn-sm" data-dismiss="modal" title="Close"><i class="fa fa-remove"></i></button>\
						</div>\
                    </div>\
                    <div class="modal-body"><p></p><div class="row"><div class="col col-sm-12"><input type="text" id="text-prompt" style="width:100%"/></div></div></div>\
                    <div class="modal-footer">\
						<div class="btn-group pull-right">\
                		<button type="button" class="btn btn-primary">Ok</button>\
                        <button type="button" data-dismiss="modal" class="btn btn-default">Cancel</button>\
        				</div>\
                    </div>\
                </div>\
            </div>\
        </div>');
        $("#bootstrap-prompt-box-modal .modal-footer .btn-primary").off('click').on('click', function () {
        	$("#bootstrap-prompt-box-modal").data('prompt-ok', true);
            $("#bootstrap-prompt-box-modal").modal('hide');
            return false;
        });
        enableModalDragging($("#bootstrap-prompt-box-modal .modal-header"));
    }
    $("#bootstrap-prompt-box-modal").off('hidden.bs.modal').on('hidden.bs.modal', function () {
    	if ($("#bootstrap-prompt-box-modal").data('prompt-ok')) {
        	var lVal = $("#bootstrap-prompt-box-modal #text-prompt").val();
        	if (lVal==='') lVal = null;
            if(callback) callback(lVal);
    	} else {
    		//cancel clicked
    		if(cancelCallback) cancelCallback();
    	}
    });
 
    $("#bootstrap-prompt-box-modal .modal-header span").html(title || "Prompt");
    $("#bootstrap-prompt-box-modal .modal-body p").html(message || "");
    var lPromptBox = $("#bootstrap-prompt-box-modal");
    lPromptBox.modal();
    showModal(lPromptBox);
    $('#bootstrap-prompt-box-modal #text-prompt').focus();
};
function showModal(pModal$) {
	var lMaxIndex = 0;
	$('.modal:visible').each(function(){
		var lTemp = parseInt($(this).css('z-index'));
		if (lMaxIndex < lTemp) lMaxIndex = lTemp;
	});
	var lZIndex = parseInt(pModal$.css('z-index'));
	if (lZIndex <= lMaxIndex)
		pModal$.css('z-index', lMaxIndex + 1);
	pModal$.modal('show');
}
function toggleModalSize(pBtn) {
	var lBtn$=$(pBtn);
	var lModal$=lBtn$.parents(".modal-dialog");
	var lIcon$ = lBtn$.find("i");
	lIcon$.removeClass("fa-expand fa-compress");
	if (lModal$.hasClass("modal-xl")) {
		lIcon$.addClass("fa-expand");
		lModal$.removeClass("modal-xl");
	} else {
		lIcon$.addClass("fa-compress");
		lModal$.addClass("modal-xl");
	}
}
function enableModalDragging(pModalHeader$) {
	pModalHeader$.on("mousedown", function(mousedownEvt) {
	    var $draggable = $(this);
	    var x = mousedownEvt.pageX - $draggable.offset().left,
	        y = mousedownEvt.pageY - $draggable.offset().top;
	    $("body").on("mousemove.draggable", function(mousemoveEvt) {
	        $draggable.closest(".modal-dialog").offset({
	            "left": mousemoveEvt.pageX - x,
	            "top": mousemoveEvt.pageY - y
	        });
	    });
	    $("body").one("mouseup", function() {
	        $("body").off("mousemove.draggable");
	    });
	    $draggable.closest(".modal").one("bs.modal.hide", function() {
	        $("body").off("mousemove.draggable");
	    });
	});

}
var remotePageCache={};
function showRemotePage(pUrl, pClass, pNoCache, pTitle) {
	if ($("#bootstrap-remote-page-modal").length == 0) {
    	$("body").append('<div id="bootstrap-remote-page-modal" class="modal fade"><div class="modal-dialog"><div class="modal-content">\
        		<div class="modal-header"><span>&nbsp;</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>\
        		<div class="modal-body"></div></div></div></div>');
    	$("#bootstrap-remote-page-modal").on('hidden.bs.modal', function (e) {
    		$("#bootstrap-remote-page-modal").find('.modal-body').empty();
    	})
    	enableModalDragging($("#bootstrap-remote-page-modal .modal-header"));
    }
    var lContent=remotePageCache[pUrl];
    var lModalRemote$=$("#bootstrap-remote-page-modal");
	if (!lContent) {
		$.ajax({
			url: pUrl,
			type: "GET",
			dataType: "html",
			success: function( pObj, pStatus, pXhr) {
				remotePageCache[pUrl]=pObj;
				showRemotePage(pUrl, pClass, pNoCache, pTitle);
			},
			error: errorHandler
		});
	} else {
		if (pNoCache)
			delete remotePageCache[pUrl];
		lModalRemote$.find('.modal-body').html(lContent);
		var lBody$ = lModalRemote$.find('.modal-dialog');
		lBody$.removeClass();
		lBody$.addClass('modal-dialog');
		if (pClass)
			lBody$.addClass(pClass);
		lModalRemote$.find('.modal-header span').html(pTitle==null?'&nbsp;':pTitle);
		lModalRemote$.modal('show',{keyboard:true});
	}
}
function closeRemotePage() {
	$("#bootstrap-remote-page-modal").modal('hide');
}
function showAlertBox(pType, pMessage, pDur) {
	
	if ($("#bootstrap-alert-box-modal").length == 0) {
    	$("body").append('<div id="bootstrap-alert-box-modal" class="alert alert-bottom text-center"><a href="#" class="close">&times;</a><span></span></div>');
    	$("#bootstrap-alert-box-modal").children(' .close').on('click', hideAlertBox);
    }
	var lModalAlert$=$("#bootstrap-alert-box-modal");
	lModalAlert$.removeClass('alert-success alert-info alert-danger alert-warning');
	lModalAlert$.addClass('alert-'+pType);
	lModalAlert$.children('span').html(pMessage);
	lModalAlert$.show(200);
	if (pDur)
		setTimeout(hideAlertBox, pDur);
}
function hideAlertBox() {
	$("#bootstrap-alert-box-modal").hide(200);
}

var errorHandler = function( pXhr, pStatus, pError ) {
	if(pXhr != null && pXhr.status == 0) {
		alert("Error connecting server\nPlease try again", "Error");
		return;
	}
	var lMsg = null;
	try {
		var lErrObj = JSON.parse(pXhr.responseText);
		lMsg = lErrObj.messages[0];
	} catch (e) {
	}
	if (!lMsg) lMsg = "Some error occurred : " + pXhr.responseText;
    alert(lMsg, "Error");
};

function htmlEscape(str) {
	if (str==null) return null;
	if (($.type(str) === 'string') || (str instanceof String)) {
	    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
	}
	return str;
}

function htmlUnescape(value){
	if (value==null) return null;
    return String(value)
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&');
}
function downloadServerFile(pUrl,pBtn$,pMethod,pData) {
	if (pBtn$) pBtn$.prop('disabled', true);
    var lXhr = new XMLHttpRequest();
    lXhr.open(pMethod?pMethod:'GET', pUrl, true);
    lXhr.responseType = 'arraybuffer';
    lXhr.setRequestHeader("loginKey", loginData==null?null:loginData.loginKey);
    lXhr.onload = function () {
        if (this.status === 200) {
            var lFilename = "";
            var lDisposition = lXhr.getResponseHeader('Content-Disposition');
            if (lDisposition && lDisposition.indexOf('attachment') !== -1) {
                var lFilenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                var lMatches = lFilenameRegex.exec(lDisposition);
                if (lMatches != null && lMatches[1]) lFilename = lMatches[1].replace(/['"]/g, '').trim();
            }
            var lType = lXhr.getResponseHeader('Content-Type');

            var lBlob = new Blob([this.response], { type: lType });
            if (typeof window.navigator.msSaveBlob !== 'undefined') {
                // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                window.navigator.msSaveBlob(lBlob, lFilename);
            } else {
                var URL = window.URL || window.webkitURL;
                var lDownloadUrl = URL.createObjectURL(lBlob);

                if (lFilename) {
                    // use HTML5 a[download] attribute to specify filename
                    var a = document.createElement("a");
                    // safari doesn't support this yet
                    if (typeof a.download === 'undefined') {
                        window.location = lDownloadUrl;
                    } else {
                        a.href = lDownloadUrl;
                        a.download = lFilename;
                        document.body.appendChild(a);
                        a.click();
                    }
                } else {
                    window.location = lDownloadUrl;
                }

                setTimeout(function () { URL.revokeObjectURL(lDownloadUrl); }, 100); // cleanup
            }
        } else {
        	alert("Download failed with error code " + this.status);
        }
        if (pBtn$) pBtn$.prop('disabled', false);
    };
    lXhr.send(pData);
}
jQuery.ajaxPrefilter(function (pOptions, pOrgOptions, jqXHR) {
    if (pOrgOptions.error) {
        pOrgOptions._error = pOrgOptions.error; 
    }
    pOptions.error = jQuery.noop();
    var lDeferred = jQuery.Deferred();
    jqXHR.done(lDeferred.resolve);
    jqXHR.fail(function () {
        var args = Array.prototype.slice.call(arguments);
        var httpStatus = jqXHR.status;
        function nextRequest(pId, pOtp) {
        	if(pId == null || pOtp == null) {
        		if (pOrgOptions._error) { 
        			lDeferred.fail(pOrgOptions._error); 
        		}
                lDeferred.rejectWith(jqXHR, args);
                return;
        	} else {
        		if(!pOrgOptions.headers)
        			pOrgOptions.headers = {};
        		pOrgOptions.headers["req-otp-id"]=pId;
        		pOrgOptions.headers["resp-otp"]=pOtp;
        		var lNewAjax = jQuery.ajax(pOrgOptions);
        		lNewAjax.then(lDeferred.resolve, lDeferred.reject);
        	}
        }
        
        if(httpStatus == OTP_IS_REQUIRED) {
            var lReqId = jqXHR.getResponseHeader("req-otp-id"); 
            __promptFOrOTP("Please Enter OTP", lReqId, nextRequest);
        } else if(httpStatus == OTP_RETRY) {
        	alert("Invalid OTP. Please try again");
            var lReqId = jqXHR.getResponseHeader("req-otp-id"); 
            __promptFOrOTP("Please Enter OTP", lReqId, nextRequest);
        } else {
            if (pOrgOptions._error) { lDeferred.fail(pOrgOptions._error); }
            lDeferred.rejectWith(jqXHR, args);
        }
    });
    return lDeferred.promise(jqXHR);
});
function __promptFOrOTP(pMessage,pReqId, pCallback) {
	prompt(pMessage?pMessage:"Please enter OTP to complete transaction ", 'OTP', function(pVal){
		if($.trim(pVal) == "") {
			alert("Please enter OTP");
			setTimeout(__promptFOrOTP(pMessage,pReqId, pCallback),2000);
			return;
		}
		pCallback(pReqId,pVal);	
	}, function () {
		pCallback(pReqId,null);
	});
}
function showQrCode($element,pCode) {
	if($.type($element) == 'string')$element = "#"+$element;
	pCode = pCode.replace(/^[\s\u3000]+|[\s\u3000]+$/g, '');
	var lType = "0", lErrorCorrectLevel = "M", lMode = "Byte", lMultiByte = "UTF-8";
	qrcode.stringToBytes = qrcode.stringToBytesFuncs[lMultiByte];
	var lQr = qrcode(lType || 4, lErrorCorrectLevel || 'M');
	lQr.addData(pCode, lMode);
	lQr.make();
	//$($element).html(lQr.createTableTag());
	//lQr.createSvgTag();
	//lQr.createImgTag();
	$($element).html(lQr.createImgTag());
}
/*xhook.after(function(request, response, callback) {
	console.log(response);
	if(response.status == OTP_IS_REQUIRED) {
		var lReqId = response.headers["req-otp-id"]; 
		__promptFOrOTP(request,response,lReqId,callback);
		return false;
	}
	callback();
	return true;
});
function __promptFOrOTP(pRequest,pResponse, pReqId, pCallback) {
	prompt("Please enter OTP to complete transaction ", 'OTP', function(pVal){
		if($.trim(pVal) == "") {
			alert("Please enter OTP");
			setTimeout(__promptFOrOTP(pRequest,pResponse ,pReqId, pCallback),2000);
			//__promptFOrOTP(pRequest,pReqId)
			return;
		}
		pRequest.xhr.setRequestHeader("req-otp-id",pReqId); 
		pRequest.xhr.setRequestHeader("resp-otp",pVal);
		pRequest.xhr.send(pRequest.body);	
	}, function () {
		pResponse.data='{"code":400,"messages":["OTP not sent"]}';
		pResponse.status=400;
		pResponse.statusText="Bad Request";
		pResponse.text='{"code":400,"messages":["OTP not sent"]}';
		pCallback();
	});
}*/
//////////////////////////////////

