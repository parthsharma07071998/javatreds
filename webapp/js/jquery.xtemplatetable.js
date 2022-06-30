!function( $ ) {
	var XTemplateTable = function(element, options) {
		var t=this;
		t.$element = $(element);
		t.options = options||this.options||{};
		t.options.elemTable$=t.options.elemTable$||t.$element.find('#elemTable');
		t.options.elemRecCount$=t.options.elemRecCount$||t.$element.find('#elemRecCount');
		t.options.elemSortCol$=t.options.elemSortCol$||t.$element.find('#elemSortCol');
		t.options.elemPageSize$=t.options.elemPageSize$||t.$element.find('#elemPageSize');
		t.options.elemCurPage$=t.options.elemCurPage$||t.$element.find('#elemCurPage');
		t.options.elemPager$=t.options.elemPager$||t.$element.find('#elemPager');
		t.options.elemSortOrd$=t.options.elemSortOrd$||t.$element.find('#elemSortOrd');
		t.indexes=[];
		t.totalPages=1;
		t.currentPage=1;
		t.sortOrder=1;
		if (t.options.sortCols) {
			populateOptions(t.options.elemSortCol$, t.options.sortCols, 1);
			t.options.elemSortCol$.off('change').on('change',function() {
				var lThis$ = $(this);
				t.sortOrder=parseInt(t.options.elemSortOrd$.val());
				t.setData(t.data,true);
			});
			populateOptions(t.options.elemSortOrd$, [{value:"1",text:"ASC"},{value:"-1",text:"DESC"}], 0);
			t.options.elemSortOrd$.off('change').on('change',function() {
				var lThis$ = $(this);
				t.sortOrder=parseInt(t.options.elemSortOrd$.val());
				t.setData(t.data,true);
			});
			t.sortOrder=parseInt(t.options.elemSortOrd$.val());
		}
		if (!t.options.pageSizes) t.options.pageSizes=[10,20,30,40,0];
		var lPageSizes = [];
		$.each(t.options.pageSizes, function(pIndex,pValue){
			lPageSizes.push({text:(pValue<=0?'All':pValue),value:pValue});
		});
		populateOptions(t.options.elemPageSize$, lPageSizes, 0);
		t.options.elemPageSize$.off('change').on('change',function() {
			var lThis$ = $(this);
			//t.display();
			t.setData(t.data,true);
		})
			
		//t.options.template
		//t.options.sortCols
	};
	
	XTemplateTable.prototype = {
		constructor: XTemplateTable,
		setData: function(pData,pReset) {
			this.indexes.splice(0,this.indexes.length);
			this.data=pData==null?[]:pData;
			this.options.elemRecCount$.html(this.data.length);
			if(typeof pReset == 'undefined')pReset = true;
			if(pReset) {
				//Determine Page Size
				this.currentPage=1;
				var lPageSize = this.options.elemPageSize$.val();
				lPageSize = parseInt(lPageSize,10);
				if(lPageSize == 0)
					this.totalPages = 1;
				else
					this.totalPages = Math.ceil(this.data.length / lPageSize) ? Math.ceil(this.data.length / lPageSize) : 1;
			}
			for(var i=0,len=this.data.length;i<len;i++)this.indexes.push(i);
			var shortCol = this.options.elemSortCol$.val() ;
			if(shortCol != "" && shortCol != null) {
				//sort data then display
				this.sortCol=this.options.elemSortCol$.val();
				this.sort();
			} else {
				this.sortCol=null;
			}
			//console.log(this.indexes);
			this.display();
		},
		display: function() {
			var t=this;
			var lData = [];
			var lStartIndex=0,lEndIndex=0;
			if(this.totalPages == 1) {
				lStartIndex = 0;
				lEndIndex = t.indexes.length;
				if(lEndIndex == 0)lEndIndex = 1;
				else if(lEndIndex < 0)lEndIndex = 0;
			} else {
				var lPageSize = t.options.elemPageSize$.val();
				lPageSize = parseInt(lPageSize,10);
				lStartIndex = lPageSize * (t.currentPage - 1);
				lEndIndex = lStartIndex + lPageSize;
				if(lEndIndex > t.indexes.length)lEndIndex = t.indexes.length;
			}
			if(t.indexes.length == 0)lEndIndex = 0;
			for(var i=lStartIndex;i < lEndIndex;i++)lData.push(t.data[this.indexes[i]]);
			//for(var i=0,len=this.indexes.length;i < len;i++)lData.push(this.data[this.indexes[i]]);
			this.options.elemTable$.html(t.options.template(lData));
			this.options.elemCurPage$.html("Showing "+(t.indexes.length>0?(lStartIndex + 1):0)+" to "+(lEndIndex)+" of " + (t.indexes.length));
			/*var o={};
			o.pages = t.totalPages;
			o.displayedPages = 4;
			o.currentPage = t.currentPage;
			o.halfDisplayed = o.displayedPages / 2;
			o.start = Math.ceil(o.currentPage > o.halfDisplayed ? Math.max(Math.min(o.currentPage - o.halfDisplayed, (o.pages - o.displayedPages)), 1) : 1);
			o.end = Math.ceil(o.currentPage > o.halfDisplayed ? Math.min(o.currentPage + o.halfDisplayed, o.pages) : Math.min(o.displayedPages, o.pages));
			*/
			
			var o={};
			o.pages = t.totalPages;
			o.displayedPages = 4;
			o.currentPage = t.currentPage;
			o.halfDisplayed = Math.ceil(o.displayedPages / 2);
			if(o.pages <= o.displayedPages) {
				o.start = 1;
				o.end = o.pages;
				if(o.start > o.end)o.start = o.end;
			} else {
				var lCalcStart = (o.currentPage - o.halfDisplayed);
				var lCalcEnd = (o.currentPage + o.halfDisplayed);
				o.start = (o.currentPage > o.halfDisplayed)? lCalcStart - (lCalcEnd > o.pages? lCalcEnd - o.pages : 0) : Math.max(lCalcStart,1);	
				o.end = (o.start == 1)?1 + o.displayedPages : Math.min((lCalcEnd + (lCalcStart < 0?lCalcStart*-1:lCalcStart==0?1:0)),o.pages);
			}
			
			//console.log(o);
			
			var lPager = t.options.elemPager$;
			$("li a",lPager).off('click');
			lPager.empty();
			if(t.indexes.length != 0) {
				if(o.currentPage != o.start)
					lPager.append($('<li><a href="javascript:void(0)" data-page-index="1">First</a></li>'));
				for (var i = o.start; i <= o.end; i++) {
					lPager.append($('<li'+(i==t.currentPage?' class="active"' : "")+'><a href="javascript:void(0)" data-page-index="'+i+'">'+i+'</a></li>'));
				}
				if(o.currentPage != o.end)
					lPager.append($('<li><a href="javascript:void(0)" data-page-index="'+t.totalPages+'">Last</a></li>'));
				
				$("li a",lPager).off('click').on('click',function(ev){
					var lThis$ = $(this);
					var lPage = lThis$.data("pageIndex");
					t.currentPage = parseInt(lPage,10);
					t.setData(t.data,false);
				});
			}
			if (t.options.postDisplay) t.options.postDisplay();
		},
		sort:function() {
			var t=this;
			/*if(t.sortCol!=null)
				t.sortOrder=(t.sortOrder==0?1:(t.sortOrder==1?-1:0));
			else
				t.sortOrder=1;*/
			if (t.sortOrder!=0)
				t.quickSort(0,t.data.length-1);
		},
		quickSort:function(pStart,pEnd)
		{
			var t=this;
			var k=Math.round((pStart+pEnd)/2);
			var i=pStart;
			var j=pEnd;
			while  (j > i)
			{
				while (t.compare(i,k))
				++i;
				while (t.compare(k,j))
				j=j - 1;
				if (i==k) k=j;
				else if (j==k) k=i;
				if (i <= j)
				{
					var lRow=t.indexes[i];
					t.indexes[i]=t.indexes[j];
					t.indexes[j]=lRow;
					++i;
					j=j - 1;
				}
			}
			if (pStart<j)
				t.quickSort(pStart, j);
			if (i<pEnd)
				t.quickSort(i, pEnd);
		},
		compare:function(pRow1,pRow2)
		{
			var t=this;
			if(t.sortCol!=null)
			{
				var lData1 = t.getProperty(t.data[t.indexes[pRow1]],this.sortCol);
				lData2 = t.getProperty(t.data[t.indexes[pRow2]],this.sortCol);
				if (t.sortOrder==1) return (lData1<lData2);
				else if (t.sortOrder==-1) return (lData1>lData2);
			}
			return false;
		},
		getProperty: function(pObj, pProp) 
		{
		    var lParts = pProp.split('.');
	        var last = lParts.pop(),
	        lLen = lParts.length,
	        lPtr = 1,
	        lCurrent = lParts[0];

	        while((pObj = pObj[lCurrent]) && lPtr < lLen) {
	        	lCurrent = lParts[i];
	        	lPtr++;
	        }

	        if(pObj) {
	            return pObj[last];
	        }
		}
	};
	$.fn.xtemplatetable = function(option, val) {
		return this.each(function() {
			var $this = $(this),
			data = $this.data('xtemplatetable'),
			options = typeof option === 'object' && option;
			if (!data) {
				data = new XTemplateTable(this, $.extend({},$.fn.xtemplatetable.defaults,options));
				$this.data('xtemplatetable', data);
			}
			if (typeof option === 'string') data[option](val);
		});
	};
}( window.jQuery );
