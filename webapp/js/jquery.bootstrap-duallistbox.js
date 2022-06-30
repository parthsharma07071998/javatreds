/*
 *  Bootstrap Duallistbox - v3.0.4
 *  A responsive dual listbox widget optimized for Twitter Bootstrap. It works on all modern browsers and on touch devices.
 *  http://www.virtuosoft.eu/code/bootstrap-duallistbox/
 *
 *  Made by István Ujj-Mészáros
 *  Under Apache License v2.0 License
 */
;(function ($, window, document, undefined) {
  // Create the defaults once
  var pluginName = 'bootstrapDualListbox',
    defaults = {
      bootstrap2Compatible: false,
      filterTextClear: 'Clear',
      filterPlaceHolder: 'Filter',
      moveSelectedLabel: 'Move selected',
      moveAllLabel: 'Move all',
      removeSelectedLabel: 'Remove selected',
      removeAllLabel: 'Remove all',
      moveOnSelect: true,                                                                 // true/false (forced true on androids, see the comment later)
      preserveSelectionOnMove: 'moved',                                                     // 'all' / 'moved' / false
      selectedListLabel: false,                                                           // 'string', false
      nonSelectedListLabel: false,                                                        // 'string', false
      helperSelectNamePostfix: '_helper',                                                 // 'string_of_postfix' / false
      selectorMinimalHeight: 100,
      showFilterInputs: true,                                                             // whether to show filter inputs
      nonSelectedFilter: '',                                                              // string, filter the non selected options
      selectedFilter: '',                                                                 // string, filter the selected options
      infoText: 'Showing all {0}',                                                        // text when all options are visible / false for no info text
      infoTextFiltered: 'Filtered {0} from {1}', // when not all of the options are visible due to the filter
      infoTextEmpty: 'Empty list',                                                        // when there are no options present in the list
      filterOnValues: false                                                               // filter by selector's values, boolean
    },
    // Selections are invisible on android if the containing select is styled with CSS
    // http://code.google.com/p/android/issues/detail?id=16922
    isBuggyAndroid = /android/i.test(navigator.userAgent.toLowerCase());

  // The actual plugin constructor
  function BootstrapDualListbox(element, options) {
    this.$element = $(element);
    var lSelect = this.$element;
    // jQuery has an extend method which merges the contents of two or
    // more objects, storing the result in the first object. The first object
    // is generally empty as we don't want to alter the default options for
    // future instances of the plugin
	var lOptions = options ? options : {};
	$.each( defaults, function( key, value ) {
	  var lData=lSelect.data(key);
	  if (lData!=null)
		  lOptions[key]=lData;
	});

    this.options = $.extend({}, defaults, lOptions);
    this._defaults = defaults;
    this._name = pluginName;
    this.build();
    this.init();
  }

  function triggerChangeEvent(dualListbox) {
    dualListbox.$element.trigger('change');
  }

  function updateSelectionStates(dualListbox) {
    dualListbox.$element.find('option').each(function(index, item) {
      var $item = $(item);
      if (typeof($item.data('original-index')) === 'undefined') {
        $item.data('original-index', dualListbox.elementCount++);
      }
      if (typeof($item.data('_selected')) === 'undefined') {
        $item.data('_selected', false);
      }
    });
  }

  function changeSelectionState(dualListbox, original_index, selected) {
    dualListbox.$element.find('option').each(function(index, item) {
      var $item = $(item);
      if ($item.data('original-index') === original_index) {
        $item.prop('selected', selected);
      }
    });
  }

  function formatString(s, args) {
    return s.replace(/\{(\d+)\}/g, function(match, number) {
      return typeof args[number] !== 'undefined' ? args[number] : match;
    });
  }

  function refreshInfo(dualListbox) {
    if (!dualListbox.options.infoText) {
      return;
    }

    var visible1 = dualListbox.elements.select1.find('option').length,
      visible2 = dualListbox.elements.select2.find('option').length,
      all1 = dualListbox.$element.find('option').length - dualListbox.selectedElements,
      all2 = dualListbox.selectedElements,
      content = '';

    if (all1 === 0) {
      content = dualListbox.options.infoTextEmpty;
    } else if (visible1 === all1) {
      content = formatString(dualListbox.options.infoText, [visible1, all1]);
    } else {
      content = formatString(dualListbox.options.infoTextFiltered, [visible1, all1]);
    }

    dualListbox.elements.info1.html(content);
    dualListbox.elements.box1.toggleClass('filtered', !(visible1 === all1 || all1 === 0));

    if (all2 === 0) {
      content = dualListbox.options.infoTextEmpty;
    } else if (visible2 === all2) {
      content = formatString(dualListbox.options.infoText, [visible2, all2]);
    } else {
      content = formatString(dualListbox.options.infoTextFiltered, [visible2, all2]);
    }

    dualListbox.elements.info2.html(content);
    dualListbox.elements.box2.toggleClass('filtered', !(visible2 === all2 || all2 === 0));
  }

  function refreshSelects(dualListbox) {
    dualListbox.selectedElements = 0;

    dualListbox.elements.select1.empty();
    dualListbox.elements.select2.empty();

    dualListbox.$element.find('option').each(function(index, item) {
      var $item = $(item);
      if ($item.prop('selected')) {
        dualListbox.selectedElements++;
        dualListbox.elements.select2.append($item.clone(true).prop('selected', $item.data('_selected')));
      } else {
        dualListbox.elements.select1.append($item.clone(true).prop('selected', $item.data('_selected')));
      }
    });

    if (dualListbox.options.showFilterInputs) {
      filter(dualListbox, 1);
      filter(dualListbox, 2);
    }
    refreshInfo(dualListbox);
  }

  function filter(dualListbox, selectIndex) {
    if (!dualListbox.options.showFilterInputs) {
      return;
    }

    saveSelections(dualListbox, selectIndex);

    dualListbox.elements['select'+selectIndex].empty().scrollTop(0);
    var regex = new RegExp($.trim(dualListbox.elements['filterInput'+selectIndex].val()), 'gi'),
      allOptions = dualListbox.$element.find('option'),
      options = dualListbox.$element;

    if (selectIndex === 1) {
      options = allOptions.not(':selected');
    } else  {
      options = options.find('option:selected');
    }

    options.each(function(index, item) {
      var $item = $(item),
        isFiltered = true;
      if (item.text.match(regex) || (dualListbox.options.filterOnValues && $item.attr('value').match(regex) ) ) {
        isFiltered = false;
        dualListbox.elements['select'+selectIndex].append($item.clone(true).prop('selected', $item.data('_selected')));
      }
      allOptions.eq($item.data('original-index')).data('filtered'+selectIndex, isFiltered);
    });

    refreshInfo(dualListbox);
  }

  function saveSelections(dualListbox, selectIndex) {
    var options = dualListbox.$element.find('option');
    dualListbox.elements['select'+selectIndex].find('option').each(function(index, item) {
      var $item = $(item);
      options.eq($item.data('original-index')).data('_selected', $item.prop('selected'));
    });
  }

  function sortOptions(select) {
    select.find('option').sort(function(a, b) {
      return ($(a).data('original-index') > $(b).data('original-index')) ? 1 : -1;
    }).appendTo(select);
  }

  function clearSelections(dualListbox) {
    dualListbox.elements.select1.find('option').each(function() {
      dualListbox.$element.find('option').data('_selected', false);
    });
  }

  function move(dualListbox) {
    if (dualListbox.options.preserveSelectionOnMove === 'all' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
      saveSelections(dualListbox, 2);
    } else if (dualListbox.options.preserveSelectionOnMove === 'moved' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
    }

    dualListbox.elements.select1.find('option:selected').each(function(index, item) {
      var $item = $(item);
      if (!$item.data('filtered1')) {
        changeSelectionState(dualListbox, $item.data('original-index'), true);
      }
    });

    refreshSelects(dualListbox);
    triggerChangeEvent(dualListbox);
    sortOptions(dualListbox.elements.select2);
  }

  function remove(dualListbox) {
    if (dualListbox.options.preserveSelectionOnMove === 'all' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
      saveSelections(dualListbox, 2);
    } else if (dualListbox.options.preserveSelectionOnMove === 'moved' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 2);
    }

    dualListbox.elements.select2.find('option:selected').each(function(index, item) {
      var $item = $(item);
      if (!$item.data('filtered2')) {
        changeSelectionState(dualListbox, $item.data('original-index'), false);
      }
    });

    refreshSelects(dualListbox);
    triggerChangeEvent(dualListbox);
    sortOptions(dualListbox.elements.select1);
  }

  function moveAll(dualListbox) {
    if (dualListbox.options.preserveSelectionOnMove === 'all' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
      saveSelections(dualListbox, 2);
    } else if (dualListbox.options.preserveSelectionOnMove === 'moved' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
    }

    dualListbox.$element.find('option').each(function(index, item) {
      var $item = $(item);
      if (!$item.data('filtered1')) {
        $item.prop('selected', true);
      }
    });

    refreshSelects(dualListbox);
    triggerChangeEvent(dualListbox);
  }

  function removeAll(dualListbox) {
    if (dualListbox.options.preserveSelectionOnMove === 'all' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 1);
      saveSelections(dualListbox, 2);
    } else if (dualListbox.options.preserveSelectionOnMove === 'moved' && !dualListbox.options.moveOnSelect) {
      saveSelections(dualListbox, 2);
    }

    dualListbox.$element.find('option').each(function(index, item) {
      var $item = $(item);
      if (!$item.data('filtered2')) {
        $item.prop('selected', false);
      }
    });

    refreshSelects(dualListbox);
    triggerChangeEvent(dualListbox);
  }

  function bindEvents(dualListbox) {
    dualListbox.elements.form.submit(function(e) {
      if (dualListbox.elements.filterInput1.is(':focus')) {
        e.preventDefault();
        dualListbox.elements.filterInput1.focusout();
      } else if (dualListbox.elements.filterInput2.is(':focus')) {
        e.preventDefault();
        dualListbox.elements.filterInput2.focusout();
      }
    });

    dualListbox.$element.on('bootstrapDualListbox.refresh', function(e, mustClearSelections){
      dualListbox.refresh(mustClearSelections);
    });

    dualListbox.elements.moveButton.on('click', function() {
      move(dualListbox);
    });

    dualListbox.elements.moveAllButton.on('click', function() {
      moveAll(dualListbox);
    });

    dualListbox.elements.removeButton.on('click', function() {
      remove(dualListbox);
    });

    dualListbox.elements.removeAllButton.on('click', function() {
      removeAll(dualListbox);
    });

    dualListbox.elements.filterInput1.on('change keyup', function() {
      filter(dualListbox, 1);
    });

    dualListbox.elements.filterInput2.on('change keyup', function() {
      filter(dualListbox, 2);
    });
  }

  BootstrapDualListbox.prototype = {
    build: function () {
      // Add the custom HTML template
      this.container = $('' +
        '<div class="bootstrap-duallistbox-container row">' +
        ' <div class="box1 col-md-6">' +
        '   <label></label>' +
        '   <span class="info-container">' +
        '     <span class="info"></span>' +
        '   </span>' +
        '   <span class="input"><i class="icon-append fa fa-filter"></i><input class="filter" type="text"></span>' +
        '   <div class="btn-group btn-group-justified buttons">' +
        '     <button class="btn btn-default btn-sm moveall">' +
        '       <i class="fa fa-chevron-right"></i>' +
        '       <i class="fa fa-chevron-right"></i>' +
        '     </button>' +
        '     <button class="btn btn-default btn-sm move">' +
        '       <i class="fa fa-chevron-right"></i>' +
        '     </button>' +
        '   </div>' +
        '   <div class="clearfix" />' +
        '   <span class="select"><select multiple="multiple"></select></span>' +
        ' </div>' +
        ' <div class="box2 col-md-6">' +
        '   <label></label>' +
        '   <span class="info-container">' +
        '     <span class="info"></span>' +
        '   </span>' +
        '   <span class="input"><i class="icon-append fa fa-filter"></i><input class="filter" type="text"></span>' +
        '   <div class="btn-group btn-group-justified buttons">' +
        '     <button class="btn btn-default btn-sm remove">' +
        '       <i class="fa fa-chevron-left"></i>' +
        '     </button>' +
        '     <button class="btn btn-default btn-sm removeall">' +
        '       <i class="fa fa-chevron-left"></i>' +
        '       <i class="fa fa-chevron-left"></i>' +
        '     </button>' +
        '   </div>' +
        '   <div class="clearfix" />' +
        '   <span class="select"><select multiple="multiple"></select></span>' +
        ' </div>' +
        '</div>')
        .insertBefore(this.$element);

      // Cache the inner elements
      this.elements = {
        originalSelect: this.$element,
        box1: $('.box1', this.container),
        box2: $('.box2', this.container),
        filterInput1: $('.box1 .filter', this.container),
        filterInput2: $('.box2 .filter', this.container),
        label1: $('.box1 > label', this.container),
        label2: $('.box2 > label', this.container),
        info1: $('.box1 .info', this.container),
        info2: $('.box2 .info', this.container),
        select1: $('.box1 select', this.container),
        select2: $('.box2 select', this.container),
        moveButton: $('.box1 .move', this.container),
        removeButton: $('.box2 .remove', this.container),
        moveAllButton: $('.box1 .moveall', this.container),
        removeAllButton: $('.box2 .removeall', this.container),
        form: $($('.box1 .filter', this.container)[0].form)
      };

      // Set select IDs
      this.originalSelectName = this.$element.attr('name') || '';
      var select1Id = 'bootstrap-duallistbox-nonselected-list_' + this.originalSelectName,
        select2Id = 'bootstrap-duallistbox-selected-list_' + this.originalSelectName;
      this.elements.select1.attr('id', select1Id);
      this.elements.select2.attr('id', select2Id);
      this.elements.label1.attr('for', select1Id);
      this.elements.label2.attr('for', select2Id);

      // Apply all settings
      this.selectedElements = 0;
      this.elementCount = 0;
      this.setFilterTextClear(this.options.filterTextClear);
      this.setFilterPlaceHolder(this.options.filterPlaceHolder);
      this.setMoveSelectedLabel(this.options.moveSelectedLabel);
      this.setMoveAllLabel(this.options.moveAllLabel);
      this.setRemoveSelectedLabel(this.options.removeSelectedLabel);
      this.setRemoveAllLabel(this.options.removeAllLabel);
      this.setMoveOnSelect(this.options.moveOnSelect);
      this.setPreserveSelectionOnMove(this.options.preserveSelectionOnMove);
      this.setSelectedListLabel(this.options.selectedListLabel);
      this.setNonSelectedListLabel(this.options.nonSelectedListLabel);
      this.setHelperSelectNamePostfix(this.options.helperSelectNamePostfix);
      this.setSelectOrMinimalHeight(this.options.selectorMinimalHeight);

      updateSelectionStates(this);

      this.setShowFilterInputs(this.options.showFilterInputs);
      this.setNonSelectedFilter(this.options.nonSelectedFilter);
      this.setSelectedFilter(this.options.selectedFilter);
      this.setInfoText(this.options.infoText);
      this.setInfoTextFiltered(this.options.infoTextFiltered);
      this.setInfoTextEmpty(this.options.infoTextEmpty);
      this.setFilterOnValues(this.options.filterOnValues);

      // Hide the original select
      this.$element.hide();

      bindEvents(this);
      refreshSelects(this);

      return this.$element;
    },
    setFilterTextClear: function(value, refresh) {
      this.options.filterTextClear = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setFilterPlaceHolder: function(value, refresh) {
      this.options.filterPlaceHolder = value;
      this.elements.filterInput1.attr('placeholder', value);
      this.elements.filterInput2.attr('placeholder', value);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setMoveSelectedLabel: function(value, refresh) {
      this.options.moveSelectedLabel = value;
      this.elements.moveButton.attr('title', value);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setMoveAllLabel: function(value, refresh) {
      this.options.moveAllLabel = value;
      this.elements.moveAllButton.attr('title', value);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setRemoveSelectedLabel: function(value, refresh) {
      this.options.removeSelectedLabel = value;
      this.elements.removeButton.attr('title', value);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setRemoveAllLabel: function(value, refresh) {
      this.options.removeAllLabel = value;
      this.elements.removeAllButton.attr('title', value);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setMoveOnSelect: function(value, refresh) {
      if (isBuggyAndroid) {
        value = true;
      }
      this.options.moveOnSelect = value;
      if (this.options.moveOnSelect) {
        this.container.addClass('moveonselect');
        var self = this;
        this.elements.select1.on('change', function() {
          move(self);
        });
        this.elements.select2.on('change', function() {
          remove(self);
        });
      } else {
        this.container.removeClass('moveonselect');
        this.elements.select1.off('change');
        this.elements.select2.off('change');
      }
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setPreserveSelectionOnMove: function(value, refresh) {
      // We are forcing to move on select and disabling preserveSelectionOnMove on Android
      if (isBuggyAndroid) {
        value = false;
      }
      this.options.preserveSelectionOnMove = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setSelectedListLabel: function(value, refresh) {
      this.options.selectedListLabel = value;
      if (value) {
        this.elements.label2.show().html(value);
      } else {
        this.elements.label2.hide().html(value);
      }
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setNonSelectedListLabel: function(value, refresh) {
      this.options.nonSelectedListLabel = value;
      if (value) {
        this.elements.label1.show().html(value);
      } else {
        this.elements.label1.hide().html(value);
      }
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setHelperSelectNamePostfix: function(value, refresh) {
      this.options.helperSelectNamePostfix = value;
      if (value) {
        this.elements.select1.attr('name', this.originalSelectName + value + '1');
        this.elements.select2.attr('name', this.originalSelectName + value + '2');
      } else {
        this.elements.select1.removeAttr('name');
        this.elements.select2.removeAttr('name');
      }
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setSelectOrMinimalHeight: function(value, refresh) {
      this.options.selectorMinimalHeight = value;
      var height = this.$element.height();
      if (this.$element.height() < value) {
        height = value;
      }
      this.elements.select1.height(height);
      this.elements.select2.height(height);
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setShowFilterInputs: function(value, refresh) {
      if (!value) {
        this.setNonSelectedFilter('');
        this.setSelectedFilter('');
        refreshSelects(this);
        this.elements.filterInput1.hide();
        this.elements.filterInput2.hide();
      } else {
        this.elements.filterInput1.show();
        this.elements.filterInput2.show();
      }
      this.options.showFilterInputs = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setNonSelectedFilter: function(value, refresh) {
      if (this.options.showFilterInputs) {
        this.options.nonSelectedFilter = value;
        this.elements.filterInput1.val(value);
        if (refresh) {
          refreshSelects(this);
        }
        return this.$element;
      }
    },
    setSelectedFilter: function(value, refresh) {
      if (this.options.showFilterInputs) {
        this.options.selectedFilter = value;
        this.elements.filterInput2.val(value);
        if (refresh) {
          refreshSelects(this);
        }
        return this.$element;
      }
    },
    setInfoText: function(value, refresh) {
      this.options.infoText = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setInfoTextFiltered: function(value, refresh) {
      this.options.infoTextFiltered = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setInfoTextEmpty: function(value, refresh) {
      this.options.infoTextEmpty = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    setFilterOnValues: function(value, refresh) {
      this.options.filterOnValues = value;
      if (refresh) {
        refreshSelects(this);
      }
      return this.$element;
    },
    getContainer: function() {
      return this.container;
    },
    getValue: function() {
    	var lVal=[];
    	var lSelected=$('option', this.elements.select2);
    	$.each(lSelected, function(pIndex) {
    		lVal.push($(this).val());
    	});
    	return lVal.length==0?null:lVal;
    },
    getOptions: function() {
		return this.options;
	},
    setValue: function(pValues) {
    	try
    	{
    		this.setSelectedFilter('', true);
        	this.refresh(true);
        	removeAll(this);
        	var lSelect=this.elements.select1;
        	if (pValues) {
    	    	$.each(pValues, function(pIndex){
    	    		lSelect.find('option[value="'+pValues[pIndex]+'"]').each(function(index, item) {
    	    			$(item).prop('selected',true);
    	    		});
    	    	});
        	}
        	move(this);
    	}catch(e)
    	{
    		console.log(this);
    		console.log(e);
    	}
    },
    focus: function() {
    	if (this.isInput) {
    		this.$element.focus();
    	} else {
    		this.$element.find('input').focus();
    	}
    },
    disable: function(){
    	this.elements.moveButton.prop('disabled',true);
    	this.elements.removeButton.prop('disabled',true);
    	this.elements.moveAllButton.prop('disabled',true);
    	this.elements.removeAllButton.prop('disabled',true);
    	this.setMoveOnSelect(false);
    },
    enable: function(){
    	this.elements.moveButton.prop('disabled',false);
    	this.elements.removeButton.prop('disabled',false);
    	this.elements.moveAllButton.prop('disabled',false);
    	this.elements.removeAllButton.prop('disabled',false);
    	this.setMoveOnSelect(true);
    },
    isDisabled: function(){
    	return this.elements.moveButton.prop('disabled');
    },
    init: function() {
    	populateOptions(this.$element,this.options.dataSetValues);
        this.refresh();
    },
    refresh: function(mustClearSelections) {
      updateSelectionStates(this);

      if (!mustClearSelections) {
        saveSelections(this, 1);
        saveSelections(this, 2);
      } else {
        clearSelections(this);
      }

      refreshSelects(this);
    },
    destroy: function() {
      this.container.remove();
      this.$element.show();
      $.data(this, pluginName, null);
      return this.$element;
    },
    isMultiValue : function(){
    	return true;
    }
  };

  // A really lightweight plugin wrapper around the constructor,
  // preventing against multiple instantiations
  $.fn[ pluginName ] = function (options) {
    var args = arguments;

    // Is the first parameter an object (options), or was omitted, instantiate a new instance of the plugin.
    if (options === undefined || typeof options === 'object') {
      return this.each(function () {
        // If this is not a select
        if (!$(this).is('select')) {
          $(this).find('select').each(function(index, item) {
            // For each nested select, instantiate the Dual List Box
            $(item).bootstrapDualListbox(options);
          });
        } else if (!$.data(this, pluginName)) {
          // Only allow the plugin to be instantiated once so we check that the element has no plugin instantiation yet

          // if it has no instance, create a new one, pass options to our plugin constructor,
          // and store the plugin instance in the elements jQuery data object.
          $.data(this, pluginName, new BootstrapDualListbox(this, options));
        }
      });
      // If the first parameter is a string and it doesn't start with an underscore or "contains" the `init`-function,
      // treat this as a call to a public method.
    } else if (typeof options === 'string' && options[0] !== '_' && options !== 'init') {

      // Cache the method call to make it possible to return a value
      var returns;

      this.each(function () {
        var instance = $.data(this, pluginName);
        // Tests that there's already a plugin-instance and checks that the requested public method exists
        if (instance instanceof BootstrapDualListbox && typeof instance[options] === 'function') {
          // Call the method of our plugin instance, and pass it the supplied arguments.
          returns = instance[options].apply(instance, Array.prototype.slice.call(args, 1));
        }
      });

      // If the earlier cached method gives a value back return the value,
      // otherwise return this to preserve chainability.
      return returns !== undefined ? returns : this;
    }

  };

})(jQuery, window, document);
