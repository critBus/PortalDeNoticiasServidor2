$(document).on('pfAjaxStart', function() {
    $.mobile.loading('show');
});

$(document).on('pfAjaxComplete', function() {
    $.mobile.loading('hide');
});

PrimeFaces.confirm = function(msg) {
    if(PrimeFaces.confirmDialog) {
        PrimeFaces.confirmSource = (typeof(msg.source) === 'string') ? $(PrimeFaces.escapeClientId(msg.source)) : $(msg.source);
        PrimeFaces.confirmDialog.showMessage(msg);
    }
    else {
        PrimeFaces.warn('No global confirmation dialog available.');
    }
}

PrimeFaces.Mobile = {
    
    navigate: function(to, cfg) {
        cfg.changeHash = cfg.changeHash||false;
        
        var targetPage = $(to);
        if(targetPage.hasClass('ui-lazypage')) {
            var pageId = to.substring(1),
            options = {
                source: pageId,
                process: pageId,
                update: pageId,
                params: [
                    {name: pageId + '_lazyload', value: true}
                ],
                oncomplete: function() {
                    $(to).page();
                    $('body').pagecontainer('change', to, cfg);
                }
            };

            PrimeFaces.ajax.Request.handle(options);
        }
        else {
            $('body').pagecontainer('change', to, cfg);
        }
    }
    
};



/**
 * PrimeFaces Mobile AccordionPanel Widget
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.tabs = this.jq.children('.ui-collapsible');
        this.headers = this.tabs.children('.ui-collapsible-heading');
        this.contents = this.tabs.children('.ui-collapsible-content');
        this.stateHolder = $(this.jqId + '_active');
        
        this.initActive();
        this.bindEvents();
        
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },
    
    initActive: function() {
        if(this.cfg.multiple) {
            var indexes = this.stateHolder.val().split(',');
            for(var i = 0; i < indexes.length; i++) {
                indexes[i] = parseInt(indexes[i]);
            }
            
            this.cfg.active = indexes;
        }
        else {
            this.cfg.active = parseInt(this.stateHolder.val());
        }
    },
    
    bindEvents: function() {
        var $this = this;
    
        this.headers.on('click.accordionPanel', function(e) {            
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                var tabIndex = element.parent().index();

                if(element.hasClass('ui-collapsible-heading-collapsed'))
                    $this.select(tabIndex);
                else
                    $this.unselect(tabIndex);
            }

            e.preventDefault();
        });
    },
    
    markLoadedPanels: function() {
        if(this.cfg.multiple) {
            for(var i = 0; i < this.cfg.active.length; i++) {
                if(this.cfg.active[i] >= 0)
                    this.markAsLoaded(this.tabs.eq(this.cfg.active[i]));
            }
        } else {
            if(this.cfg.active >= 0)
                this.markAsLoaded(this.tabs.eq(this.cfg.active));
        }
    },
    
    select: function(index) {
        var tab = this.tabs.eq(index);

        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, tab);
            if(result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(tab);

        if(this.cfg.multiple)
            this.addToSelection(index);
        else
            this.cfg.active = index;

        this.saveState();

        if(shouldLoad) {
            this.loadDynamicTab(tab);
        }
        else {
            this.show(tab);
            this.fireTabChangeEvent(tab);
        }

        return true;
    },
    
    show: function(tab) {
        var header = tab.children('.ui-collapsible-heading'),
        content = tab.children('.ui-collapsible-content');

        //deactivate current
        if(!this.cfg.multiple) {
            this.close(this.tabs.filter(':not(.ui-collapsible-collapsed)'));
        }

        tab.removeClass('ui-collapsible-collapsed').attr('aria-expanded', true);
        header.removeClass('ui-collapsible-heading-collapsed')
                .children('.ui-collapsible-heading-toggle').removeClass('ui-icon-plus').addClass('ui-icon-minus');
        content.removeClass('ui-collapsible-content-collapsed').attr('aria-hidden', false).show();
        PrimeFaces.invokeDeferredRenders(this.id);
    },
    
    close: function(tab) {
        tab.addClass('ui-collapsible-collapsed').attr('aria-expanded', false);
        tab.children('.ui-collapsible-heading').addClass('ui-collapsible-heading-collapsed')
                .children('.ui-collapsible-heading-toggle').addClass('ui-collapsible-heading-collapsed').removeClass('ui-icon-minus').addClass('ui-icon-plus');
        tab.children('.ui-collapsible-content').attr('aria-hidden', true)
                .addClass('ui-collapsible-content-collapsed').attr('aria-hidden', true).hide();
    },

    unselect: function(index) {
        this.close(this.tabs.eq(index))

        this.removeFromSelection(index);
        this.saveState();
        
        if(this.hasBehavior('tabClose')) {
            this.fireTabCloseEvent(tab);
        }
    },
    
    addToSelection: function(nodeId) {
        this.cfg.active.push(nodeId);
    },

    removeFromSelection: function(index) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return (r !== index);
        });
    },
    
    saveState: function() {
        if(this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    },
    
    loadDynamicTab: function(tab) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: tab.attr('id')},
                {name: this.id + '_tabindex', value: tab.index()}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            tab.find('> .ui-collapsible-content > p').html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(tab);
                            }   
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(tab);
            }
        };

        if(this.hasBehavior('tabChange')) {
            this.cfg.behaviors['tabChange'].call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    fireTabChangeEvent : function(tab) {
        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'],
            ext = {
                params: [
                    {name: this.id + '_newTab', value: tab.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(tab.index())}
                ]
            };

            tabChangeBehavior.call(this, ext);
        }        
    },

    fireTabCloseEvent : function(tab) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: [
                {name: this.id + '_tabId', value: tab.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(tab.index())}
            ]
        };
        
        tabCloseBehavior.call(this, ext);
    },
    
    markAsLoaded: function(tab) {
        tab.data('loaded', true);
    },

    isLoaded: function(tab) {
        return tab.data('loaded') === true;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }
    
});
/**
 * PrimeFaces Mobile AutoComplete Widget
 */
PrimeFaces.widget.AutoComplete = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.minLength = (this.cfg.minLength !== undefined) ? this.cfg.minLength : 1;
        this.cfg.delay = (this.cfg.delay !== undefined) ? this.cfg.delay : 300;
        this.inputContainer = this.jq.children('.ui-input-search');
        this.input = $(this.jqId + '_input');
        this.hinput = $(this.jqId + '_hinput');
        this.clearIcon = this.inputContainer.children('.ui-input-clear');
        this.cfg.pojo = (this.hinput.length === 1);
        this.panel = this.jq.children('.ui-controlgroup');
        this.itemContainer = this.panel.children('.ui-controlgroup-controls');
        
        this.bindEvents();
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {
        var $this = this;

        this.input.on('keyup.autoComplete', function(e) {
            var value = $this.input.val();

            if(value.length === 0) {
                $this.hide();
            }
            else {
                $this.showClearIcon();
            }

            if(value.length >= $this.cfg.minLength) {
                //Cancel the search request if user types within the timeout
                if($this.timeout) {
                    clearTimeout($this.timeout);
                }

                $this.timeout = setTimeout(function() {
                    $this.search(value);
                }, $this.cfg.delay);
            }
        });
        
        this.clearIcon.on('click.autoComplete', function(e) {
            $this.input.val('');
            $this.hinput.val('');
            $this.hide();
        });
    },
    
    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.on('click.autoComplete', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value');

            $this.input.val(item.attr('data-item-label')).focus();

            if($this.cfg.pojo) {
                $this.hinput.val(itemValue); 
            }

            $this.fireItemSelectEvent(event, itemValue);
            $this.hide();
        });
    },
    
    search: function(query) {
        //allow empty string but not undefined or null
        if(query === undefined || query === null) {
            return;
        }

        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.itemContainer.html(content);

                        this.showSuggestions();
                    }
                });
                
                return true;
            }
        };

        options.params = [
            {name: this.id + '_query', value: query}  
        ];
        
        if(this.hasBehavior('query')) {
            var queryBehavior = this.cfg.behaviors['query'];
            queryBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    show: function() {
        this.panel.removeClass('ui-screen-hidden');
    },
    
    hide: function() {        
        this.panel.addClass('ui-screen-hidden');
        this.hideClearIcon();
    },
    
    showSuggestions: function() {
        this.items = this.itemContainer.children('.ui-autocomplete-item');                   
        this.bindDynamicEvents();
                
        if(this.items.length) {
            this.items.first().addClass('ui-first-child');
            this.items.last().addClass('ui-last-child');
            
            if(this.panel.is(':hidden')) {
                this.show();
            }
        }
        else {
            if(this.cfg.emptyMessage) { 
                var emptyText = '<div class="ui-autocomplete-emptyMessage ui-widget">'+this.cfg.emptyMessage+'</div>';
                this.itemContainer.html(emptyText);
            }
            else {
                this.hide();
            }
        }
    },
    
    fireItemSelectEvent: function(event, itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.cfg.behaviors['itemSelect'].call(this, ext);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    },
    
    showClearIcon: function() {
        this.clearIcon.removeClass('ui-input-clear-hidden');
    },
    
    hideClearIcon: function() {
        this.clearIcon.addClass('ui-input-clear-hidden');
    }
    
});
/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.inline = !this.cfg.popup;
        this.input = $(this.jqId + '_input');
        var $this = this;

        this.configureLocale();

        //disabled dates
        this.cfg.beforeShowDay = function(date) { 
            if($this.cfg.preShowDay)
                return $this.cfg.preShowDay(date);
            else if($this.cfg.disabledWeekends)
                return $.datepicker.noWeekends(date);
            else
                return [true,''];
        }
        
        this.bindEvents();

        if(!this.cfg.disabled) {
            this.input.date(this.cfg);
        }
                        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
        
    refresh: function(cfg) {
        this.init(cfg);
    },
    
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.locale];

        if(localeSettings) {
            for(var setting in localeSettings) {
                if(localeSettings.hasOwnProperty(setting)) {
                    this.cfg[setting] = localeSettings[setting];
                }
            }
        }
    },
    
    bindEvents: function() {
        var $this = this;

        this.cfg.onSelect = function() {
            $this.fireDateSelectEvent();
            
            setTimeout( function(){
                $this.input.date( "addMobileStyle" );
            },0);
        };
    },
    
    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },
    
    setDate: function(date) {
        this.input.date('setDate', date);
    },
    
    getDate: function() {
        return this.input.date('getDate');
    },
    
    enable: function() {
        this.input.date('enable');
    },
    
    disable: function() {
        this.input.date('disable');
    }
    
});
/**
 * PrimeFaces Mobile DataList Widget
 */
PrimeFaces.widget.DataList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.list = $(this.jqId + '_list');
        this.content = this.jq.children('.ui-datalist-content');
        this.list.listview();
        this.items = this.list.children('li');
        
        if(this.content.prevAll("[class^='ui-bar']").length) {
            this.jq.addClass('ui-datalist-topbar');
        }
        
        if(this.content.nextAll("[class^='ui-bar']").length) {
            this.jq.addClass('ui-datalist-bottombar');
        }
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.paginator) {
            this.bindPaginator();
        }
        
        if(this.cfg.behaviors) {
            var $this = this;
            
            $.each(this.cfg.behaviors, function(eventName, fn) {
                $this.items.on(eventName, function() {
                    var ext = {
                        params: [{name: $this.id + '_item', value: $(this).index()}]
                    };
                            
                    fn.call($this, ext);
                });
            });
        }
    },
    
    bindPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.list = $(this.jqId + '_list');
                            this.list.listview();
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];
            pageBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
     
    getPaginator: function() {
        return this.paginator;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    }
});
/**
 * PrimeFaces Mobile DataGrid Widget
 */
PrimeFaces.widget.DataGrid = PrimeFaces.widget.BaseWidget.extend({
        
    init: function(cfg) {
        this._super(cfg);
        this.content = $(this.jqId + '_content');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.paginator) {
            this.bindPaginator();
        }
    },
    
    bindPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];
            pageBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
     
    getPaginator: function() {
        return this.paginator;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }
    
        return false;
    }
});
/**
 * PrimeFaces Mobile DataTable Widget
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.BaseWidget.extend({
    
    SORT_ORDER: {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    },
    
    init: function(cfg) {
        this._super(cfg);
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.paginator) {
            this.bindPaginator();
        }
        
        this.bindSortEvents();
        
        if(this.cfg.selectionMode) {
            this.bindSelection();
        }
        
        this.bindMobileEvents();
    },
    
    bindPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    bindSortEvents: function() {
        var $this = this;
        this.sortableColumns = this.thead.find('> tr > th.ui-sortable-column');

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null;
    
            if(sortIcon.hasClass('ui-column-sorted')) {
                if(sortIcon.hasClass('ui-icon-arrow-u'))
                    sortOrder = this.SORT_ORDER.ASCENDING;
                else
                    sortOrder = this.SORT_ORDER.DESCENDING;
            }
            else {
                sortOrder = this.SORT_ORDER.UNSORTED;
            }
            
            columnHeader.data('sortorder', sortOrder);
        }
        
        this.sortableColumns.on('click.dataTable', function(e) {
            if(!$this.shouldSort(e, this)) {
                return;
            }

            PrimeFaces.clearSelection();
                            
            var columnHeader = $(this),
            sortOrderData = columnHeader.data('sortorder'),
            sortOrder = (sortOrderData === $this.SORT_ORDER.UNSORTED) ? $this.SORT_ORDER.ASCENDING : -1 * sortOrderData;
            
            $this.sort(columnHeader, sortOrder);
        });
        
        var reflowDD = $(this.jqId + '_reflowDD');
        if(reflowDD && this.cfg.reflow) {
            reflowDD.selectmenu();
            
            reflowDD.change(function(e) {
                var arrVal = $(this).val().split('_');
                if(arrVal.length > 1) {
                    var columnHeader = $this.sortableColumns.eq(parseInt(arrVal[0])),
                    sortOrder = parseInt(arrVal[1]);
                    
                    columnHeader.data('sortorder', sortOrder);
                    columnHeader.trigger('click.dataTable');
                }
            });
        }
    },
    
    bindMobileEvents: function() {
        if(this.cfg.behaviors) {
            var $this = this,
            rowSelector = '> tr:not(.ui-datatable-empty-message)';
            
            $.each(this.cfg.behaviors, function(eventName, fn) {
                $this.tbody.off(eventName, rowSelector).on(eventName, rowSelector, null, function() {
                    var rowMeta = $this.getRowMeta($(this));
        
                    var ext = {
                        params: [{name: $this.id + '_rowkey', value: rowMeta.key}]
                    };
                            
                    fn.call($this, ext);
                });
            });
        }
    },
    
    shouldSort: function(event) {
        if(this.isEmpty()) {
            return false;
        }
                
        return $(event.target).is('th,span');
    },
    
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];

            pageBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    sort: function(columnHeader, order) {  
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_sortKey', value: columnHeader.attr('id')},
                     {name: this.id + '_sortDir', value: order}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.paginator) {
                                this.paginator.setPage(0, true);
                            }
                                                        
                            this.sortableColumns.filter('.ui-column-sorted').data('sortorder', this.SORT_ORDER.UNSORTED).removeClass('ui-column-sorted')
                                            .find('.ui-sortable-column-icon').removeClass('ui-icon-arrow-d ui-icon-arrow-u').addClass('ui-icon-bars');

                            columnHeader.data('sortorder', order).addClass('ui-column-sorted');
                            
                            var sortIcon = columnHeader.find('.ui-sortable-column-icon');
                            if(order === this.SORT_ORDER.DESCENDING) {
                                sortIcon.removeClass('ui-icon-bars ui-icon-arrow-u').addClass('ui-icon-arrow-d');
                            } else if(order === this.SORT_ORDER.ASCENDING) {
                                sortIcon.removeClass('ui-icon-bars ui-icon-arrow-d').addClass('ui-icon-arrow-u');
                            }
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {           
                if($this.paginator && args && $this.paginator.cfg.rowCount !== args.totalRecords) {
                    $this.paginator.setTotalRecords(args.totalRecords);
                }
            }
        };
        
        options.params.push({name: this.id + '_sortKey', value: columnHeader.attr('id')});
        options.params.push();

        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];

            sortBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    bindSelection: function() {
        var $this = this;
        this.selectionHolder = $(this.jqId + '_selection');
        this.rowSelector = '> tr.ui-datatable-selectable';

        var preselection = this.selectionHolder.val();
        this.selection = (preselection === "") ? [] : preselection.split(',');

        this.tbody.off('click.dataTable', this.rowSelector).on('click.dataTable', this.rowSelector, null, function(e) {
            $this.onRowClick(e, this);
        });
    },
    
    onRowClick: function(event, rowElement) { 
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('td,span:not(.ui-c)')) {
            var row = $(rowElement),
            selected = row.hasClass('ui-bar-b');
    
            if(selected) {
                this.unselectRow(row);
            }
            else {
                if(this.cfg.selectionMode === 'single') {
                    this.unselectAllRows();
                }
                
                this.selectRow(row);
            }
        }
    },
    
    selectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        row.addClass('ui-bar-b');
        
        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },
    
    unselectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        row.removeClass('ui-bar-b');
        
        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },
    
    unselectAllRows: function() {
        this.tbody.children('tr.ui-bar-b').removeClass('ui-bar-b');
        
        this.selection = [];
        this.writeSelections();
    },
    
    /**
     * Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
     */
    fireRowSelectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors[behaviorEvent];

            if(selectBehavior) {
                var ext = {
                        params: [{name: this.id + '_instantSelectedRowKey', value: rowKey}
                    ]
                };

                selectBehavior.call(this, ext);
            }
        }
    },
    
    /**
     * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
     */
    fireRowUnselectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors[behaviorEvent];

            if(unselectBehavior) {
                var ext = {
                    params: [
                    {
                        name: this.id + '_instantUnselectedRowKey', 
                        value: rowKey
                    }
                    ]
                };

                unselectBehavior.call(this, ext);
            }
        }
    },
    
    writeSelections: function() {
        this.selectionHolder.val(this.selection.join(','));
    },
    
    findRow: function(r) {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row;
    },
    
    /**
     * Remove given rowIndex from selection
     */
    removeSelection: function(rowIndex) {        
        this.selection = $.grep(this.selection, function(value) {
            return value != rowIndex;
        });
    },
    
    /**
     * Adds given rowIndex to selection if it doesn't exist already
     */
    addSelection: function(rowIndex) {
        if(!this.isSelected(rowIndex)) {
            this.selection.push(rowIndex);
        }
    },
    
    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },
    
    /**
     * Finds if given rowIndex is in selection
     */
    isSelected: function(rowIndex) {
        return PrimeFaces.inArray(this.selection, rowIndex);
    },
        
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length === 1;
    },
    
    updateData: function(data) {
        this.tbody.html(data);
        
        this.postUpdateData();
    },
    
    postUpdateData: function() {
        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        } 
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    }
    
});
/**
 * PrimeFaces Mobile Dialog Widget
 */
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.popupElement = this.jq.children('.ui-popup');
        this.mask = this.jq.prev('.ui-popup-screen');
        this.content = this.popupElement.children('.ui-content');
        this.header = this.popupElement.children('.ui-header');
        this.closeIcon = this.header.children('.ui-icon-delete');
        
        //cleanup duplicate masks due to ajax update
        var orphanMask = this.mask.prev('.ui-popup-screen');
        if(orphanMask.length) {
            orphanMask.remove();
        } 
        
        this.popupElement.popup({
            positionTo: 'window',
            dismissible: false,
            overlayTheme: 'b',
            enhanced: true
        });
    
        this.bindEvents();
    },
        
    bindEvents: function() {
        var $this = this;
        
        this.closeIcon.on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });
    },
    
    show: function() {
        this.popupElement.popup('open', {transition:this.cfg.showEffect});
    },
    
    hide: function() {
        this.popupElement.popup('close');
    }
});

/**
 * PrimeFaces Mobile ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = PrimeFaces.widget.Dialog.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.title = this.header.children('.ui-title');
        this.message = this.content.children('.ui-title');

        if(this.cfg.global) {
            PrimeFaces.confirmDialog = this;

            this.content.find('.ui-confirmdialog-yes').on('click.ui-confirmdialog', function(e) {                
                if(PrimeFaces.confirmSource) {
                    var fn = new Function('event',PrimeFaces.confirmSource.data('pfconfirmcommand'));
                    
                    fn.call(PrimeFaces.confirmSource.get(0),e);
                    PrimeFaces.confirmDialog.hide();
                    PrimeFaces.confirmSource = null;
                }
                
                e.preventDefault();
            });

            this.jq.find('.ui-confirmdialog-no').on('click.ui-confirmdialog', function(e) {
                PrimeFaces.confirmDialog.hide();
                PrimeFaces.confirmSource = null;
                
                e.preventDefault();
            });
        }
    },

    applyFocus: function() {
        this.jq.find(':button,:submit').filter(':visible:enabled').eq(0).focus();
    },
            
    showMessage: function(msg) {
        if(msg.header)
            this.title.text(msg.header);
        
        if(msg.message)
            this.message.text(msg.message);
        
        this.show();
    }

});
/**
 * PrimeFaces Mobile InputText Widget
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.input = this.jq.children('input');
        this.cfg.enhanced = true;
        this.cfg.clearBtn = true;
        
        this.input.textinput(this.cfg);
    }
    
});

/**
 * PrimeFaces Mobile InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.enhanced = true;
        this.cfg.autogrow = false;
        
        this.jq.textinput(this.cfg);
    }
    
});

/**
 * PrimeFaces Mobile Password Widget
 */
PrimeFaces.widget.Password = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.input = this.jq.children('input');
        this.cfg.enhanced = true;
        this.cfg.clearBtn = true;
        
        this.input.textinput(this.cfg);
    }
    
});

/**
 * PrimeFaces SelectOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.controlGroup = this.jq.children('.ui-controlgroup-controls');
        this.buttons = this.controlGroup.find('> .ui-radio > label.ui-btn');
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
                
        this.buttons.on('click.selectOneButton', function(e) {
            var button = $(this);

            if(!button.hasClass('ui-btn-active')) {
                $this.select(button);
            }
        });
    },
    
    select: function(button) {
        this.buttons.filter('.ui-btn-active').removeClass('ui-btn-active').next().prop('checked', false);

        button.addClass('ui-btn-active').next().prop('checked', true).change();
    }
    
});

/**
 * PrimeFaces SelecyManyButton Widget
 */
PrimeFaces.widget.SelectManyButton = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.controlGroup = this.jq.children('.ui-controlgroup-controls ');
        this.buttons = this.controlGroup.find('> .ui-checkbox > label.ui-btn');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        this.buttons.on('click.selectManyButton', function() {
            var button = $(this);

            if(button.hasClass('ui-btn-active'))
                $this.unselect(button);
            else
                $this.select(button);
        });
    },
    
    select: function(button) {
        button.addClass('ui-btn-active').next().prop('checked', true).change();

    },
    
    unselect: function(button) {
        button.removeClass('ui-btn-active').next().prop('checked', false).change();
    }
    
});

/**
 * PrimeFaces Mobile InputSlider Widget
 */
PrimeFaces.widget.InputSlider = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.jq.slider();
    }
    
});

/**
 * PrimeFaces Mobile RangeSlider Widget
 */
PrimeFaces.widget.RangeSlider = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.jq.attr('data-role', 'rangeslider');
        this.jq.rangeslider();
    }
    
});

/**
 * PrimeFaces Mobile UISwitch Widget
 */
PrimeFaces.widget.UISwitch = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.input = this.jq.children('input');
        this.cfg.enhanced = true;
        
        this.input.flipswitch(this.cfg);
    }
    
});

/**
 * PrimeFaces Mobile InputSwitch Widget
 */
PrimeFaces.widget.InputSwitch = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.input = this.jq.children('input');
        this.cfg.enhanced = true;
        
        this.input.flipswitch(this.cfg);
    }
    
});

/**
 * PrimeFaces Mobile SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.children('select').selectmenu(this.cfg);
    }
    
});

/**
 * PrimeFaces Mobile SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.controlgroup();
    }
    
});

/**
 * PrimeFaces Mobile SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.controlgroup();
    }
    
});

/**
 * PrimeFaces Mobile SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.label = this.jq.children('label');
        this.input = this.jq.children(':checkbox');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.label.on('click.selectBooleanCheckbox', function() {
            $this.toggle();
        });
    },
    
    toggle: function() {
        if(this.input.prop('checked'))
            this.uncheck();
        else
            this.check();
    },
    
    check: function() {
        this.label.removeClass('ui-checkbox-off').addClass('ui-checkbox-on');
    },
    
    uncheck: function() {
        this.label.removeClass('ui-checkbox-on').addClass('ui-checkbox-off');
    }
});

/**
 * PrimeFaces Mobile SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.nativeMenu = false;
        
        this.jq.children('select').selectmenu(this.cfg);
    }
    
});


/**
 * PrimeFaces Mobile Growl Widget
 */
PrimeFaces.widget.Growl = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.initOptions(cfg);
        
        this.jq.popup({
            positionTo: 'window',
            theme: 'b',
            overlayTheme: 'b'
        });
        
        this.container = $(this.jqId + '-popup');
        this.popupContainer = this.container.find('> div.ui-popup');
        this.popupContainer.append('<p></p>');
        this.messageContainer = this.popupContainer.children('p');
        this.placeholder = $(this.jqId + '-placeholder');
        
        this.popupContainer.removeAttr('id');
        this.placeholder.attr('id', this.id);
        
        this.show(this.cfg.msgs);
    },
    
    initOptions: function(cfg) {
        this.cfg = cfg;
        this.cfg.sticky = this.cfg.sticky||false;
        this.cfg.life = this.cfg.life||6000;
        this.cfg.escape = (this.cfg.escape === false) ? false : true;
    },
    
    refresh: function(cfg) {
    	this.initOptions(cfg);
        this.show(cfg.msgs);
    },
    
    show: function(msgs) {
        var $this = this;

        this.removeAll();

        if(msgs.length) {
            $.each(msgs, function(index, msg) {
                $this.renderMessage(msg);
            });

            this.jq.popup('open', {transition:'pop'});
            
            if(!this.cfg.sticky) {
                this.setRemovalTimeout();
            }
        }
    },
    
    removeAll: function() {
        this.messageContainer.children().remove();
    },
    
    renderMessage: function(msg) {
        var markup = '<div class="ui-growl-item ui-grid-a">';
        markup += '<div class="ui-growl-severity ui-block-a"><a class="ui-btn ui-shadow ui-corner-all ui-btn-icon-notext ui-btn-b ui-btn-inline" href="#"></a></div>';
        markup += '<div class="ui-growl-message ui-block-b">';
        markup += '<div class="ui-growl-summary"></div>';
        markup += '<div class="ui-growl-detail"></div>';
        markup += '</div></div>';
        
        var item = $(markup),
        severityEL = item.children('.ui-growl-severity'),
        summaryEL = item.find('> .ui-growl-message > .ui-growl-summary'),
        detailEL = item.find('> .ui-growl-message > .ui-growl-detail');

        severityEL.children('a').addClass(this.getSeverityIcon(msg.severity));
        
        if(this.cfg.escape) {
            summaryEL.text(msg.summary);
            detailEL.text(msg.detail);
        }
        else {
            summaryEL.html(msg.summary);
            detailEL.html(msg.detail);
        }
                
        this.messageContainer.append(item);
    },
    
    getSeverityIcon: function(severity) {
        var icon;
        
        switch(severity) {
            case 'info':
                icon = 'ui-icon-info';
                break;
            break;
    
            case 'warn':
                icon = 'ui-icon-alert';
                break;
            break;
    
            case 'error':
                icon = 'ui-icon-delete';
                break;
            break;
    
            case 'fatal':
                icon = 'ui-icon-delete';
                break;
            break;
        }
        
        return icon;
    },
        
    setRemovalTimeout: function() {
        var $this = this;
        
        if(this.timeout) {
            clearTimeout(this.timeout);
        }
        
        this.timeout = setTimeout(function() {
            $this.jq.popup('close');
        }, this.cfg.life);
    }
});

/**
 * PrimeFaces Mobile Menu Widget
 */
PrimeFaces.widget.PlainMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.items = this.jq.children('li');
        this.items.filter(':first-child').addClass('ui-first-child');
        this.items.filter(':last-child').addClass('ui-last-child');
    }
});

/**
 * PrimeFaces Mobile TabMenu Widget
 */
PrimeFaces.widget.TabMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.links = this.jq.find('a.ui-link');
        this.links.eq(this.cfg.activeIndex).addClass('ui-btn-active');
        
        this.jq.navbar();
    }
});
/**
 * PrimeFaces Mobile Paginator Widget
 * 
 */
PrimeFaces.widget.Paginator = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.jq = $();

        var $this = this;
        $.each(this.cfg.id, function(index, id){
            $this.jq = $this.jq.add($(PrimeFaces.escapeClientId(id)));
        });

        //elements
        this.controlGroups = this.jq.find('> .ui-controlgroup > .ui-controlgroup-controls');
        this.pageLinks = this.controlGroups.children('.ui-paginator-page');
        this.firstLink = this.controlGroups.children('.ui-paginator-first');
        this.prevLink  = this.controlGroups.children('.ui-paginator-prev');
        this.nextLink  = this.controlGroups.children('.ui-paginator-next');
        this.endLink   = this.controlGroups.children('.ui-paginator-last');

        //metadata
        this.cfg.rows = (this.cfg.rows === 0) ? this.cfg.rowCount : this.cfg.rows;
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        this.cfg.pageLinks = this.cfg.pageLinks||10;
        this.cfg.currentPageTemplate = this.cfg.currentPageTemplate||'({currentPage} of {totalPages})';

        //event bindings
        this.bindEvents();
    },
            
    bindEvents: function() {
        var $this = this;

        //page links
        this.bindPageLinkEvents();

        //First page link
        this.firstLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage(0);
            }
        });

        //Prev page link
        this.prevLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page - 1);
            }
        });

        //Next page link
        this.nextLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.page + 1);
            }
        });

        //Last page link
        this.endLink.click(function() {
            PrimeFaces.clearSelection();

            if(!$(this).hasClass("ui-state-disabled")){
                $this.setPage($this.cfg.pageCount - 1);
            }
        });
    },
            
    bindPageLinkEvents: function(){
        var $this = this;

        this.pageLinks.on('click.paginator', function(e) {
            var link = $(this);

            if(!link.hasClass('ui-state-disabled') &&! link.hasClass('ui-btn-active')) {
                $this.setPage(parseInt(link.text()) - 1);
            }
        });
    },
    
    updateUI: function() {  
        //boundaries
        if(this.cfg.page === 0) {
            this.disableElement(this.firstLink);
            this.disableElement(this.prevLink);
        }
        else {
            this.enableElement(this.firstLink);
            this.enableElement(this.prevLink);
        }

        if(this.cfg.page === (this.cfg.pageCount - 1)) {
            this.disableElement(this.nextLink);
            this.disableElement(this.endLink);
        }
        else {
            this.enableElement(this.nextLink);
            this.enableElement(this.endLink);
        }
        
        this.updatePageLinks();
    },
            
    updatePageLinks: function() {
        var start, end, delta, j = 0;

        //calculate visible page links
        this.cfg.pageCount = Math.ceil(this.cfg.rowCount / this.cfg.rows)||1;
        var visiblePages = Math.min(this.cfg.pageLinks, this.cfg.pageCount);

        //calculate range, keep current in middle if necessary
        start = Math.max(0, Math.ceil(this.cfg.page - ((visiblePages) / 2)));
        end = Math.min(this.cfg.pageCount - 1, start + visiblePages - 1);

        //check when approaching to last page
        delta = this.cfg.pageLinks - (end - start + 1);
        start = Math.max(0, start - delta);

        //update dom
        for(var i = start; i <= end; i++) {
            var styleClass = 'ui-paginator-page ui-btn';
            
            if(this.cfg.page === i) {
                styleClass += " ui-btn-active";
            }
            
            this.pageLinks.eq(j).attr('class', styleClass).text(i + 1);
            this.pageLinks.eq(parseInt(j + (this.pageLinks.length / 2))).attr('class', styleClass).text(i + 1);
            j++;
        }
    },
            
    setPage: function(p, silent) {
        if(p >= 0 && p < this.cfg.pageCount && this.cfg.page != p){        
            var newState = {
                first: this.cfg.rows * p,
                rows: this.cfg.rows,
                page: p
            };

            if(silent) {
                this.cfg.page = p;
                this.updateUI();
            }
            else {
                this.cfg.paginate.call(this, newState);
            }
        }
    },
              
    setTotalRecords: function(value) {
        this.cfg.rowCount = value;
        this.cfg.pageCount = Math.ceil(value / this.cfg.rows)||1;
        this.cfg.page = 0;
        this.updateUI();
    },
            
    getCurrentPage: function() {
        return this.cfg.page;
    },
    
    getFirst: function() {
        return (this.cfg.rows * this.cfg.page);
    },
            
    getContainerHeight: function(margin) {
        var height = 0;
        
        for(var i = 0; i < this.jq.length; i++) {
            height += this.jq.eq(i).outerHeight(margin);
        }
        
        return height;
    },
            
    disableElement: function(element) {
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
        element.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled').attr('tabindex', -1);
    },
            
    enableElement: function(element) {
        element.removeClass('ui-state-disabled').attr('tabindex', 0);
    }
});
/**
 * PrimeFaces Mobile OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.showEvent = this.cfg.showEvent||'click.overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.overlaypanel';
        this.cfg.target = this.cfg.targetId ? $(PrimeFaces.escapeClientId(this.cfg.targetId)): null;
        
        this.jq.panel({
            position: this.cfg.at,
            display: this.cfg.showEffect,
            dismissable: this.cfg.dismissable
        });
        
        if(this.cfg.dynamic) {
            this.jq.append('<div class="ui-panel-inner"></div>');
            this.content = this.jq.children('div.ui-panel-inner');
        }

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        if(this.cfg.target) {
            if(this.cfg.showEvent === this.cfg.hideEvent) {
                this.cfg.target.on(this.cfg.showEvent, function(e) {
                    $this.toggle();
                });
            }
            else {
                this.cfg.target.on(this.cfg.showEvent, function(e) {
                    $this.show();
                })
                .on(this.cfg.hideEffect, function(e) {
                    $this.hide();
                });
            }
        }
    },
    
    show: function() {
        if(!this.loaded && this.cfg.dynamic)
            this.loadContents();
        else
            this._show();
    },
    
    _show: function() {
        this.jq.panel('open');
        
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },
    
    hide: function() {
        this.jq.panel('close');
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },
    
    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    },
    
    loadContents: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    isVisible: function() {
        this.jq.is(':visible');
    }
});

/**
 * PrimeFaces Mobile Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('.ui-panel-m-titlebar');
        this.content = this.jq.children('.ui-panel-m-content');
        this.onshowHandlers = this.onshowHandlers||{};
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        if(this.cfg.toggleable) {
            this.toggler = this.header.children('.ui-panel-m-titlebar-icon');
            this.toggleStateHolder = $(this.jqId + '_collapsed');
            
            this.toggler.on('click', function(e) {
                $this.toggle();
                
                e.preventDefault();
            });
        }
    },
    
    toggle: function() {
        if(this.content.is(':visible'))
            this.collapse();
        else
            this.expand();
    },
    
    collapse: function() {
        this.toggleState(true, 'ui-icon-minus', 'ui-icon-plus');
        this.content.hide();
    },
    
    expand: function() {
        this.toggleState(false, 'ui-icon-plus', 'ui-icon-minus');
        this.content.show();
        this.invokeOnshowHandlers();
    },
    
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);
        this.fireToggleEvent();
    },
    
    fireToggleEvent: function() {
        if(this.cfg.behaviors) {
            var toggleBehavior = this.cfg.behaviors['toggle'];
            
            if(toggleBehavior) {
                toggleBehavior.call(this);
            }
        }
    },
    
    addOnshowHandler: function(id, fn) {
        this.onshowHandlers[id] = fn;
    },
    
    invokeOnshowHandlers: function() {
        for(var id in this.onshowHandlers) {
            if(this.onshowHandlers.hasOwnProperty(id)) {
                var fn = this.onshowHandlers[id];
                
                if(fn.call()) {
                    delete this.onshowHandlers[id];
                }
            }
        }
    }
});
/**
 * PrimeFaces Mobile TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.BaseWidget.extend({
    
    GRID_MAP: {
        '2': 'a',
        '3': 'b',
        '4': 'c',
        '5': 'd'
    },
    
    BLOCK_MAP: {
        '0': 'a',
        '1': 'b',
        '2': 'c',
        '3': 'd',
        '4': 'e'
    },
    
    init: function(cfg) {
        this._super(cfg);
        this.navbar = this.jq.children('.ui-navbar');
        this.navContainer = this.navbar.children('.ui-tabs-nav');
        this.headers = this.navContainer.children('.ui-tabs-header');
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.onshowHandlers = this.onshowHandlers||{};
        this.initGrid();
        
        this.bindEvents();
        
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }
        
    },
    
    initGrid: function() {
        var tabcount = this.headers.length;
        
        this.navContainer.addClass('ui-grid-' + this.GRID_MAP[tabcount.toString()]);
        
        for(var i = 0; i < tabcount; i++) {
            this.headers.eq(i).addClass('ui-block-' + this.BLOCK_MAP[(i % 5).toString()]);
        }
    },
    
    bindEvents: function() {
        var $this = this;

        //Tab header events
        this.headers.children('a')
                .on('click.tabView', function(e) {
                    var element = $(this),
                    index = element.parent().index();

                    if(!element.hasClass('ui-state-disabled') && (index !== $this.cfg.selected)) {
                        $this.select(index);
                    }

                    e.preventDefault();
                });

    },
    
    select: function(index, silent) {
        if(this.cfg.onTabChange && !silent) {
            var result = this.cfg.onTabChange.call(this, index);
            if(result === false)
                return false;
        }

        var newPanel = this.panelContainer.children().eq(index),
        shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);

        this.stateHolder.val(index);
        this.cfg.selected = index;

        if(shouldLoad) {
            this.loadDynamicTab(newPanel);
        }
        else {
            this.show(newPanel);
            
            if(this.hasBehavior('tabChange') && !silent) {
                this.fireTabChangeEvent(newPanel);
            }
        }

        return true;
    },
    
    show: function(newPanel) {
        var oldHeader = this.headers.filter('.ui-tabs-active'),
        newHeader = this.headers.eq(newPanel.index()),
        oldPanel = this.panelContainer.children(':visible');

        oldPanel.attr('aria-hidden', true);
        oldHeader.attr('aria-expanded', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);

        oldHeader.removeClass('ui-tabs-active').children('a').removeClass('ui-btn-active');
        oldPanel.hide();

        newHeader.addClass('ui-tabs-active').children('a').addClass('ui-btn-active');
        newPanel.show();

        this.postTabShow(newPanel);
    },
    
    fireTabChangeEvent: function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.index()}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },
    
    loadDynamicTab: function(newPanel) {
        var $this = this,
        tabindex = newPanel.index(),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: tabindex}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            newPanel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(newPanel);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(newPanel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            this.cfg.behaviors['tabChange'].call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        //execute onshowHandlers and remove successful ones
        for(var id in this.onshowHandlers) {
            if(this.onshowHandlers.hasOwnProperty(id)) {
                var fn = this.onshowHandlers[id];
                
                if(fn.call()) {
                    delete this.onshowHandlers[id];
                }
            }
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },
    
    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    }
    
});
